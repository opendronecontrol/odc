/**
* 
* Max external for piloting ARDrone using OpenDroneControl with JavaDrone backend
*
* fishuyo - 2013
*
*/

import org.opendronecontrol.platforms.ardrone.ARDrone
import org.opendronecontrol.spatial._

import com.cycling74.max._
import com.cycling74.jitter._
import MaxObject._

import java.net._

class OpenDroneControl extends MaxObject {


  declareAttribute("ip")

  // default ip
  var ip = "192.168.1.1"

  // javadrone api ARDrone
  var drone = new ARDrone

  // tracking controller
  var tracking = drone.tracking

  // hold last video frame as jitter matrix
  private var mat: JitterMatrix = _


  println("OpenDroneControl version 0.1")
  
  LoadLibraryPath.unsafeAddDir(".") //hack to make native libraries visible in current directory
  LoadLibraryPath.unsafeAddDir("./lib") //hack to make native libraries visible in ./lib directory



  //////////////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////////////// 

  def connect(){
    drone.ip = ip
    val _this = this
    val t = new Thread(){
      override def run(){
        _this.drone.connect 
      }
    }
    t.start
  }
  
  def disconnect() = drone.disconnect
  def reset() = drone.reset
  def takeOff() = drone.takeOff
  def land() = drone.land

  def toggleFly() = drone.toggleFly

  // def led(anim:Int, freq:Float, dur:Int) = drone.playLed(anim, freq, dur)
  // def dance(anim:Int, dur:Int) = drone.dance(anim, dur)
  
  def move( x: Float, y: Float, z: Float, rv: Float ){
    tracking.stop
    drone.move(x,y,z,rv) 
  }
  def forward(v:Float) = move(0,0,-v,0)
  def back(v:Float) = move(0,0,v,0)
  def left(v:Float) = move(-v,0,0,0)
  def right(v:Float) = move(v,0,0,0)
  def up(v:Float) = move(0,v,0,0)
  def down(v:Float) = move(0,-v,0,0)
  def cw(v:Float) = move(0,0,0,v)
  def ccw(v:Float) = move(0,0,0,-v)

  def hover = { tracking.stop; drone.hover }

  def reboot() = drone.reboot()

  def telnet(command:String) = drone.telnet(command)


  /*
  * PositionTracker commands
  */

  def lookAt( x:Float, y:Float, z:Float ){
    tracking.isLookingAt = true
    tracking.lookAtDest = false
    tracking.lookingAt = Vec3(x,y,z)
  }
  def dontLook(){
    tracking.isLookingAt = false
    tracking.lookAtDest = false
  }

  def moveTo( x:Float,y:Float,z:Float,qx:Float,qy:Float,qz:Float,qw:Float ){
    tracking.moveTo( Pose(Vec3(x,y,z),Quat(qw,qx,qy,qz)) )
  }
  def moveTo( x:Float,y:Float,z:Float,w:Float=0.f ){
    moveTo( Pose(Vec3(x,y,z),Quat().fromEuler((0.f,w*math.Pi.toFloat/180.f,0.f)) ) )
  }
  def moveTo( p:Pose ) = tracking.moveTo(p)

  def addWaypoint( x:Float,y:Float,z:Float,w:Float ) = tracking.addWaypoint(x,y,z,w)
  def clearWaypoints() = tracking.clearWaypoints
  
  def step(x:Float,y:Float,z:Float,qx:Float,qy:Float,qz:Float,qw:Float){
    step( Pose(Vec3(x,y,z),Quat(qw,qx,qy,qz)))
  }  
  def step(x:Float,y:Float,z:Float,w:Float=0.f ){
    step( Pose(Vec3(x,y,z),Quat().fromEuler((0.f,w*math.Pi.toFloat/180.f,0.f))) )
  }

  def step(p:Pose) = tracking.step(p)

  def stepUsingInternalSensors() = tracking.stepUsingInternalSensors()

  def setPDGainsXZ( p1:Float, d1:Float, dd:Float ) = {
    tracking.posKp.set( p1, tracking.posKp.y, p1)
    tracking.posKd.set( d1, tracking.posKd.y, d1)
    tracking.posKdd.set( dd, tracking.posKdd.y, dd)
  }
  def setPDGainsY( p1:Float, d1:Float, dd:Float ) = {
    tracking.posKp.y = p1
    tracking.posKd.y = d1
    tracking.posKdd.y = dd
  }
  def setPDGainsRot( p1:Float, d1:Float ) = {
    tracking.rotKp = p1
    // tracking.rotKd = d1
  }

  def tracking( args:Array[Atom] ){
    val com = args(0).getString
    com match {
      case "posThresh" => drone.tracking.posThresh = args(1).getFloat
      case "yawThresh" => drone.tracking.yawThresh = args(1).getFloat
      case "rotateFirst" => drone.tracking.yawThresh = args(1).getInt
      case "useHover" => drone.tracking.yawThresh = args(1).getInt
      case "patrol" => drone.tracking.yawThresh = args(1).getInt
      case _ => println(s"unknown command $com for tracking module")
    }
  }


  /*
  * Drone Configuration
  */

  def config(value:Array[Atom]){
    var a:Any = null
    var name = value(0).getString

    if( value.length > 1){
      if( value(1).isInt ) a = value(1).getInt
      else if( value(1).isFloat ) a = value(1).getFloat
      else if( value(1).isString ) a = value(1).getString
    }

    drone.config(name,a)
  }

  def setConfigOption(name:String,value:String){
    drone.setConfigOption(name,value)
  }

  def getVersion() = drone.getVersion() 


  /*
  * General command interface
  *  This interprets any unmatched message sent to the max object
  *  as a general command sent directly to the drone backend
  */
  override def anything(msg:String, args:Array[Atom]){
    var list = List[Any]()
    for( i<-(0 until args.length)){
      if( args(i).isInt ) list = args(i).getInt :: list
      else if( args(i).isFloat ) list = args(i).getFloat :: list
      else if( args(i).isString ) list = args(i).getString :: list
    }
    drone.command(msg, list: _*)
  }


  /*
  * Sensor Data
  */ 

  // prints all available sensor names
  def listSensors(){
    if( !drone.hasSensors){
      println("no sensor data received")
      return
    }
    drone.sensors.listSensors()
  }

  // outputs sensor name and value out of the right outlet
  def sensor( name:String ){
    if( !drone.hasSensors){
      println("no sensor data received")
      return
    }
    drone.sensors(name).value match {
      case v:Float => outlet(1, Array[Atom](Atom.newAtom(name),Atom.newAtom(v)))
      case v:Int => outlet(1, Array[Atom](Atom.newAtom(name),Atom.newAtom(v)))
      case v:Boolean => outlet(1, Array[Atom](Atom.newAtom(name),Atom.newAtom(v)))
      case v:Vec3 => outlet(1, Array[Atom](Atom.newAtom(name),Atom.newAtom(v.x),Atom.newAtom(v.y),Atom.newAtom(v.z)))
    }
  }

  /*
  * OSC Interface
  */
  def osc( args:Array[Atom] ){
    val com = args(0).getString
    com match {
      case "start" => if( args.length > 1 ) drone.osc.start(args(1).getInt) else drone.osc.start()
      case "stop" => drone.osc.stop
      case "sendSensors" => drone.osc.sendSensors(args(1).getString, args(2).getInt)
      case _ => println(s"unknown command $com for osc module")
    }
  }

  /*
  * Video Interface
  */
  def video( args:Array[Atom] ){
    if( !drone.hasVideo ){
      println("No video stream detected, please connect to a drone with a video stream")
      return
    }
    val com = args(0).getString
    com match {
      case "start" => drone.video.start
      case "stop" => drone.video.stop
      case _ => println(s"unknown command $com for video module")
    }
  }

  // bang outputs latest video frame
  override def bang(){
    if( drone.hasVideo && drone.video.getFrame() != null ){
      if( mat == null ) mat = new JitterMatrix
      mat.copyBufferedImage(drone.video.getFrame())
      outlet(0,"jit_matrix",mat.getName())
    }else post("no frames received.")
  }


  // When mxj object deleted
  override def notifyDeleted(){
    disconnect
  }

}




object LoadLibraryPath{
    // HACK: adds dir to load library path
  def unsafeAddDir(dir: String) = try {
    val field = classOf[ClassLoader].getDeclaredField("usr_paths")
    field.setAccessible(true)
    val paths = field.get(null).asInstanceOf[Array[String]]
    if(!(paths contains dir)) {
      field.set(null, paths :+ dir)
      System.setProperty("java.library.path",
       System.getProperty("java.library.path") +
       java.io.File.pathSeparator +
       dir)
    }
  } catch {
    case _: IllegalAccessException =>
      error("Insufficient permissions; can't modify private variables.")
    case _: NoSuchFieldException =>
      error("JVM implementation incompatible with path hack")
  }
}