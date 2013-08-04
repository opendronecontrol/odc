/**
* 
* Max external for piloting ARDrone using the JavaDrone api
*
* fishuyo - 2012
*
*/

import org.opendronecontrol.platforms.ardrone.ARDrone
import org.opendronecontrol.tracking.PositionTrackingController
import org.opendronecontrol.spatial._

import com.cycling74.max._
import com.cycling74.jitter._
import MaxObject._

import java.net._

class DroneControl extends MaxObject {

  declareAttribute("ip")

  // default ip
  var ip = "192.168.1.1"

  // javadrone api ARDrone
  var drone = new ARDrone

  // tracking controller
  var control = new PositionTrackingController(drone)

  // hold last video frame as jitter matrix
  private var mat: JitterMatrix = _


  println("OpenDroneControl version 0.4.3")
  

  //////////////////////////////////////////////////////////////////////
  // member functions
  //////////////////////////////////////////////////////////////////////

  def setPDGainsXZ( p1:Float, d1:Float, dd:Float ) = {
    control.posKp.set( p1, control.posKp.y, p1)
    control.posKd.set( d1, control.posKd.y, d1)
    control.posKdd.set( dd, control.posKdd.y, dd)
  }
  def setPDGainsY( p1:Float, d1:Float, dd:Float ) = {
    control.posKp.y = p1
    control.posKd.y = d1
    control.posKdd.y = dd
  }
  def setPDGainsRot( p1:Float, d1:Float ) = {
    control.rotKp = p1
    // control.rotKd = d1
  }

  def connect(){
    drone.ip = ip
    drone.connect
  }

  def disconnect() = drone.disconnect

  def clearEmergency() = drone.clearEmergency

  def trim() = drone.trim
  
  def takeOff() = drone.takeOff
  
  def land() = drone.land

  def toggleFly() = drone.toggleFly

  def playLed(anim:Int, freq:Float, dur:Int) = drone.playLed(anim, freq, dur)

  def dance(anim:Int, dur:Int) = drone.dance(anim, dur)
  

  def move( lr: Float, fb: Float, udv: Float, rv: Float ){
    control.navigating = false
    drone.move(lr,udv,fb,rv) 
  }

  def lookAt( x:Float, y:Float, z:Float ){
    control.isLookingAt = true
    control.lookAtDest = false
    control.lookingAt = Vec3(x,y,z)
  }
  def dontLook(){
    control.isLookingAt = false
    control.lookAtDest = false
  }

  def moveTo( x:Float,y:Float,z:Float,qx:Float,qy:Float,qz:Float,qw:Float ){
    moveTo( Pose(Vec3(x,y,z),Quat(qw,qx,qy,qz)) )
  }
  def moveTo( x:Float,y:Float,z:Float,w:Float=0.f ){
    moveTo( Pose(Vec3(x,y,z),Quat().fromEuler((0.f,w*math.Pi.toFloat/180.f,0.f)) ) )
  }
  def moveTo( p:Pose ) = control.moveTo(p)
  

  def hover = { control.navigating=false; drone.hover }

  def addWaypoint( x:Float,y:Float,z:Float,w:Float ) = control.addWaypoint(x,y,z,w)
  def clearWaypoints() = control.clearWaypoints

  // used to set ARDrone config options
  def setConfigOption(name:String,value:String){
    drone.setConfigOption(name,value)
  }
  def setMaxEuler(v:Float) = drone.setMaxEuler(v)
  def setMaxVertical(v:Float) = drone.setMaxVertical(v)
  def setMaxRotation(v:Float) = drone.setMaxRotation(v)

  def getVersion() = drone.getVersion() 


  def selectCameraMode( mode: Int) = drone.selectCameraMode(mode)

  
  def reboot() = drone.reboot()

  def telnet(command:String) = drone.telnet(command)

 
  def step(x:Float,y:Float,z:Float,qx:Float,qy:Float,qz:Float,qw:Float){
    step( Pose(Vec3(x,y,z),Quat(qw,qx,qy,qz)))
  }  
  def step(x:Float,y:Float,z:Float,w:Float=0.f ){
    step( Pose(Vec3(x,y,z),Quat().fromEuler((0.f,w*math.Pi.toFloat/180.f,0.f))) )
  }

  def step(p:Pose) = control.step(p)


  def getPos(){ outlet(1, Array[Atom](Atom.newAtom("pos"),Atom.newAtom(control.tPose.pos.x),Atom.newAtom(control.tPose.pos.y),Atom.newAtom(control.tPose.pos.z))) }
  def getVel(){ outlet(1, Array[Atom](Atom.newAtom("vel"),Atom.newAtom(control.tVel.x),Atom.newAtom(control.tVel.y),Atom.newAtom(control.tVel.z))) }
  def getGyroAngles(){ outlet(1, Array[Atom](Atom.newAtom("gyroAngles"),Atom.newAtom(drone.gyroAngles.x),Atom.newAtom(drone.gyroAngles.y),Atom.newAtom(drone.gyroAngles.z))) }

  override def bang(){
    if( drone.frame != null ){
      if( mat == null ) mat = new JitterMatrix
      mat.copyBufferedImage(drone.frame)
      outlet(0,"jit_matrix",mat.getName())
    }else post("no frames received.")
  }

  override def notifyDeleted(){
    disconnect
  }

}