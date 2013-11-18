
package org.opendronecontrol
package tracking

import spatial._
import drone._
import drone.sensors._

import scala.collection.mutable.Queue

// /** PositionController Module
//   *   this is a mixin trait for DroneBase that adds absolute positioning based on some position sensors or tracking system
//   */
// trait PositionControllerMixin extends DroneBase {
//   val tracker = new PositionTrackingController(this)
//   def moveTo(p:Pose){ tracker.moveTo(p) }
//   def step(p:Pose){ tracker.step(p) }
// }



/** PositionTrackingController
  *   Used to handle tracking system state and calculate control parameters pased on a destination position and yaw
  *    using a simple proportional derivative controller
  */
class PositionTrackingController( val drone:DroneBase ) {

  // PID controllers
  // val positionPID = new PIDController[Vec3]
  // val rotationPID = new PIDController[Float]
  // positionPID.setGains(Vec3(.5f,1.f,.5f),Vec3(0),Vec3(300.f,3.f,300.f))
  // rotationPID.setGains(.318f,0.f,3.f)

  // current state flags
  var (navigating, goingHome) = (false,false)

  // initial pose stored when first received tracking data
  var homePose = Pose()

  // number of frames receiving the same position
  var dropped = 0

  // navigation state
  var lookAtDest = false
  var isLookingAt = false
  var lookingAt:Vec3 = null
  var waypoints = new Queue[(Float,Float,Float,Float)]

  //state for controller
  var destPose = Pose()
  var tPose = Pose()
  var tVel = Vec3()
  var tAcc = Vec3()

  var posError = Vec3()
  var posErrorIntegral = Vec3()
  var posErrorDerivative = Vec3()
  var posErrorDDerivative = Vec3()
  var posKp = Vec3(.5f,1.f,.5f)
  var posKi = Vec3(0.f,0.f,0.f)
  var posKd = Vec3(10.f,1.f,10.f)
  var posKdd = Vec3(0.f,0.f,0.f)

  var yawError = 0.f
  var rotKp = .318f
  var rotKd = 0.f

  // controller outputs (% of maximum)(delta relates to angVel and to jerk)
  var control = Vec3()  // x -> left/right tilt, y -> up/down throttle, z -> forward/back tilt, 
  var rot = 0.f         // rotate ccw/cw speed

  var posThresh = .01f  // threshold distance from destination or waypoint (meters)
  var yawThresh = 10.f // threshold rotation from destination yaw (degrees)
  
  var rotFirst = false  // rotate first before other movement
  var useHover = false  // use the hover command as default non action
  var patrol = false   // loop waypoints

  var t0:Long = 0
  var dt = 0.f
  
  //////////////////////////////////////////////////////////////////////
  // member functions
  //////////////////////////////////////////////////////////////////////

  // set drone to set yaw oriented toward a point in space
  def lookAt( x:Float, y:Float, z:Float ){
    isLookingAt = true
    lookAtDest = false
    lookingAt = Vec3(x,y,z)
  }

  // reset look behaviour to default destPose yaw
  def dontLook(){
    isLookingAt = false
    lookAtDest = false
  }

  // tell the drone to move to a point in space and orient its yaw based on a quaternion
  def moveTo( x:Float,y:Float,z:Float,qx:Float,qy:Float,qz:Float,qw:Float ){
    moveTo( Pose(Vec3(x,y,z),Quat(qw,qx,qy,qz)) )
  }
  def moveTo( x:Float,y:Float,z:Float,w:Float=0.f ){
    moveTo( Pose(Vec3(x,y,z),Quat().fromEuler((0.f,w*math.Pi.toFloat/180.f,0.f)) ) )
  }
  def moveTo( p:Pose ){
    destPose = Pose(p)
    navigating = true
  }

  def stop() = navigating = false

  // add a waypoint of moveTo locations to a queue
  def addWaypoint( x:Float,y:Float,z:Float,w:Float ) = {
    if( waypoints.size > 1000 ) waypoints.clear
    waypoints.enqueue((x,y,z,w))
    navigating = true
  }

  // trigger the drone to move to the next waypoint in the queue,
  // done automatically when close to previous waypoint destination
  def nextWaypoint(){
    if( waypoints.isEmpty ) return
    // drone.playLED(4,10,1)
    val (x,y,z,w) = waypoints.dequeue
    moveTo(x,y,z,w)
    if( patrol ) waypoints.enqueue((x,y,z,w))  // push last point to end of queue to cycle
  }

  // delete all waypoints
  def clearWaypoints() = waypoints.clear


  // update seen position and calculate next control parameter using simple PD controllers
  // step function must be called at a regular interval of about 30 ms for best results
  
  def step(x:Float,y:Float,z:Float,qx:Float,qy:Float,qz:Float,qw:Float){
    step( Pose(Vec3(x,y,z),Quat(qw,qx,qy,qz)))
  }  
  def step(x:Float,y:Float,z:Float,w:Float=0.f ){
    step( Pose(Vec3(x,y,z),Quat().fromEuler((0.f,w*math.Pi.toFloat/180.f,0.f))) )
  }

  def step(p:Pose){

    if( drone.hasSensors() ){
      drone.sensors.set(Position(p.pos))
      drone.sensors.set(Sensor[Quat]("quat",p.quat))
    }

    if( homePose == null ) homePose = Pose(p)    // save initial pose for later
    if( !navigating ) return                     // only run if navigating flag set

    var hover = useHover                         // default non action flag
    rot = 0.f                                    // zero rotation control value
    control.zero()                                // zero translation control value

    // update tracked velocity
    tVel = p.pos - tPose.pos
    tPose.set(p)

    // if drone not moving for consequtive frames it is likely the data isn't reliable
    if( tVel.x == 0.f && tVel.y == 0.f && tVel.z == 0.f ){
      dropped += 1
      if(dropped > 5){
        println("lost tracking or step size too short!!!") // TODO
        if( hover ) drone.hover
        return
      }
    }else dropped = 0

    /*** rotation controller ***/
    var destRotVec = Vec3()
    var rotVec = p.uf().normalize()              // current direction vector of drone

    
    if(lookAtDest) destRotVec.set( (destPose.pos - tPose.pos).normalize )        // look where it's going
    else if( isLookingAt ) destRotVec.set( (lookingAt - tPose.pos).normalize )   // look at point
    else destRotVec.set( destPose.uf().normalize )                              // look in destination pose direction

    yawError = math.acos((destRotVec dot rotVec)).toFloat
    if( (rotVec cross destRotVec).y > 0.f ) yawError *= -1.f     // negate error if cross product points up (destination to left)

    // set rot control value if error over threshold
    if( math.abs(yawError) > (yawThresh * math.Pi/180.f) ){ 
      hover = false
      rot = yawError * rotKp    // Proportional control onlny
      if( math.abs(rot) > 1.f) rot = rot / math.abs(rot) // limit control value to [-1,1]
    }


    /*** position controller ***/
    val err = (destPose.pos - tPose.pos)
    val derr = err - posError
    posErrorDDerivative = derr - posErrorDerivative
    posErrorDerivative = derr
    posErrorIntegral += err
    posError.set(err)

    // set translate control values if error over threshold distance
    val dist = err.mag
    if( dist  > posThresh ){
      hover = false

      // PD controller update
      val worldControl = posError*posKp + posErrorIntegral*posKi + posErrorDerivative*posKd + posErrorDDerivative*posKdd

      // convert control values to drone's frame of reference
      // assumes drone oriented 0 degrees looking down negative z axis, positive x axis to its right(-90 degrees), 
      control.x = worldControl dot tPose.quat.toX // left right control
      control.y = worldControl dot tPose.quat.toY // up down control
      control.z = worldControl dot tPose.quat.toZ // forward backward control

      // limit control values to range [-1,1]
      if( math.abs(control.x) > 1.f) control.x = control.x / math.abs(control.x)
      if( math.abs(control.y) > 1.f) control.y = control.y / math.abs(control.y)
      if( math.abs(control.z) > 1.f) control.z = control.z / math.abs(control.z)
      
    }else nextWaypoint

    if(hover) drone.hover
    else if(rotFirst && rot != 0.f) drone.move(0,0,0,rot)
    else drone.move(control.x,control.y,control.z,rot)      
  }

  def stepUsingInternalSensors(){
    if( !drone.hasSensors() ){
      // println("no sensors detected")
      return
    }

    if( t0 == 0 ){ 
      t0 = System.currentTimeMillis()
      return
    }
    val t = System.currentTimeMillis()
    dt = (t - t0).toFloat
    t0 = t

    var pose = Pose()
    var localVel = Vec3()

    if( drone.sensors.hasSensor("velocity") ){
      localVel = drone.sensors("velocity").vec * .001f
    }

    if( drone.sensors.hasSensor("gyroscope")){
      val euler = drone.sensors("gyroscope").vec * Vec3(1,-1,-1) * (math.Pi/180.f).toFloat // to radians as expected for Quat

      val quat = Quat().fromEuler(euler)
      pose.quat.set(quat)

      // calculate velocity in world frame from gyroscope measured orientation
      val worldVel = quat.toX * -localVel.y + quat.toY*localVel.z + quat.toZ*localVel.x //TODO check direction of veloctiy is correct
      val pos = tPose.pos + worldVel // * dt
      pose.pos.set(pos)
    }

    if( drone.sensors.hasSensor("altimeter")){
      pose.pos.y = drone.sensors("altimeter").float
    }

    step(pose)
  }

  // experimenting incomplete..
  def stepUsingGyroscope(){
    if( !drone.hasSensors() ){
      // println("no sensors detected")
      return
    }

    if( t0 == 0 ){ 
      t0 = System.currentTimeMillis()
      return
    }
    val t = System.currentTimeMillis()
    dt = (t - t0).toFloat
    t0 = t

    var pose = Pose()

    if( drone.sensors.hasSensor("gyroscope")){
      val euler = drone.sensors("gyroscope").vec * Vec3(1,-1,-1) * (math.Pi/180.f).toFloat // to radians as expected for Quat

      val quat = Quat().fromEuler(euler)
      pose.quat.set(quat)

    }

    if( drone.sensors.hasSensor("altimeter")){
      pose.pos.y = drone.sensors("altimeter").float
    }

    step(pose)
  }

  def stepVision( pos:Vec3, yawOffset:Float ){

    //position passed in from camera positioned above the fly zone

    if( !drone.hasSensors() ){
      // println("no sensors detected")
      return
    }

    if( t0 == 0 ){ 
      t0 = System.currentTimeMillis()
      return
    }
    val t = System.currentTimeMillis()
    dt = (t - t0).toFloat
    t0 = t

    var pose = Pose()
    pose.pos.set(pos.x, pos.z, pos.y)


    var localVel = Vec3()

    if( drone.sensors.hasSensor("velocity") ){
      localVel = drone.sensors("velocity").vec * .001f
    }

    if( drone.sensors.hasSensor("gyroscope")){
      var euler = drone.sensors("gyroscope").vec * Vec3(1,-1,-1)
      euler.y += yawOffset
      euler = euler * (math.Pi/180.f).toFloat // to radians as expected for Quat

      val quat = Quat().fromEuler(euler)
      pose.quat.set(quat)
    } else {
      return
    }

    if( drone.sensors.hasSensor("altimeter")){
      pose.pos.y = drone.sensors("altimeter").float
    }

    step(pose)
  }




}



