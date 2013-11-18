

package org.opendronecontrol
package platforms.ardrone

import spatial._
import drone._
import drone.sensors._
import drone.video.VideoStream

import net.SimpleTelnetClient

import com.codeminders.ardrone.{ARDrone => JD}
import com.codeminders.ardrone._

import java.net._
import java.awt.image.BufferedImage

import scala.collection.mutable.Queue

class ARDrone(var ip:String="192.168.1.1") extends DroneBase {

  // current state flags
  var (connecting, ready ) = (false,false)

  // javadrone api ARDrone
  var drone : Option[JD] = None
  
  // controller params
  var maxEuler = 0.3f  //(0 - .52 rad)
  var maxVert = 1.f   //(0.2 - 2. m/s)
  var maxRot = 3.0f    //(.7 - 6.11 rad/s)


  def connect(){
    if( drone.isDefined ){
      println("Drone already connected.")
      return
    }else if( connecting ){
      return
    }
    connecting = true
    val _this = this
    // val t = new Thread(){
      // override def run(){
        try {
          val d = new JD( InetAddress.getByName(ip), 1000, 1000 )
           println("connecting to ARDrone at " + ip + " ..." )
          d.connect
          d.clearEmergencySignal
          d.waitForReady(3000)
          println("ARDrone connected and ready!")
          d.trim
          _this.videoStream = Some(new ARDroneVideoStream(d))
          _this.sensorData = Some(new ARDroneSensorData(d))
          d.addImageListener(_this.videoStream.get.asInstanceOf[DroneVideoListener])
          d.addNavDataListener(_this.sensorData.get.asInstanceOf[NavDataListener])
          drone = Some(d)
          connecting = false
          ready = true

        } catch {
          case e: Exception => println("Drone connection failed."); e.printStackTrace 
          connecting = false
        }  
      // }
    // }
    // t.start
  }

  def disconnect(){
    if( drone.isEmpty ){
      println("Drone not connected.")
      return
    }
    if( sensors("flying").bool ) drone.get.land
    drone.get.disconnect
    drone = None
    ready = false
    println("Drone disconnected.") 
  }

  override def reset(){
    clearEmergency()
  }

  def takeOff(){
    if( drone.isEmpty ){
      println("Drone not connected.")
      return
    }
    drone.get.takeOff
  }

  def land(){
    if( drone.isEmpty ){
      println("Drone not connected.")
      return
    }
    drone.get.land
  }

  def move( x: Float, y: Float, z: Float, r: Float ){
    if( drone.isEmpty ){
      println("Drone not connected.")
      return
    }
    drone.get.move(x,z,y,r) 
  }

  override def hover(){
    if( drone.isEmpty ){
      println("Drone not connected.")
      return
    }
    drone.get.hover
  }

  /**
    * Implemented commands:
    *
    * command: led [pattern:Int] [frequency:Float] [duration(seconds):Int]
    * {{{
    *   patterns:
    *           BLINK_GREEN_RED(0), BLINK_GREEN(1), BLINK_RED(2), BLINK_ORANGE(3), SNAKE_GREEN_RED(4), FIRE(5), STANDARD(6), RED(
    *            7), GREEN(8), RED_SNAKE(9), BLANK(10), RIGHT_MISSILE(11), LEFT_MISSILE(12), DOUBLE_MISSILE(13), FRONT_LEFT_GREEN_OTHERS_RED(
    *            14), FRONT_RIGHT_GREEN_OTHERS_RED(15), REAR_RIGHT_GREEN_OTHERS_RED(16), REAR_LEFT_GREEN_OTHERS_RED(17), LEFT_GREEN_RIGHT_RED(
    *            18), LEFT_RED_RIGHT_GREEN(19), BLINK_STANDARD(20);
    * }}}
    *
    * command: animation [pattern:Int] [duration(seconds):Int]
    * {{{
    *    patterns:
    *            PHI_M30_DEG(0)
    *            PHI_30_DEG(1)
    *            THETA_M30_DEG(2)
    *            THETA_30_DEG(3)
    *            THETA_20DEG_YAW_200DEG(4)
    *            THETA_20DEG_YAW_M200DEG(5)
    *            TURNAROUND(6)
    *            TURNAROUND_GODOWN(7)
    *            YAW_SHAKE(8)
    *            YAW_DANCE(9)
    *            PHI_DANCE(10)
    *            THETA_DANCE(11)
    *            VZ_DANCE(12)
    *            WAVE(13)
    *            PHI_THETA_MIXED(14)
    *            DOUBLE_PHI_THETA_MIXED(15)
    *            Flip Forward(16) (2.0 only)
    *            Flip Back(17) (2.0 only)
    *            Flip Left(18) (2.0 only)
    *            Flip Right(19) (2.0 only)
    *            ANIM_MAYDAY(20)
    * }}}
    *
    */
  override def command(com:String, args:Any*){
    com match {
      case "help" => println("""
          commands:
            led
            animation
        """)
      case "led" => args match {
        case Seq(a:Int,f:Float,d:Int, xs @ _ *) => playLed(a,f,d)
        case Seq("help", xs @ _ *) => println( """
              command: led [pattern:Int] [frequency:Float] [duration(seconds):Int]
              patterns:
                BLINK_GREEN_RED(0), BLINK_GREEN(1), BLINK_RED(2), BLINK_ORANGE(3), SNAKE_GREEN_RED(4), FIRE(5), STANDARD(6), RED(
                7), GREEN(8), RED_SNAKE(9), BLANK(10), RIGHT_MISSILE(11), LEFT_MISSILE(12), DOUBLE_MISSILE(13), FRONT_LEFT_GREEN_OTHERS_RED(
                14), FRONT_RIGHT_GREEN_OTHERS_RED(15), REAR_RIGHT_GREEN_OTHERS_RED(16), REAR_LEFT_GREEN_OTHERS_RED(17), LEFT_GREEN_RIGHT_RED(
                18), LEFT_RED_RIGHT_GREEN(19), BLINK_STANDARD(20);
            """)
        case _ => println("command: led [pattern:Int] [frequency:Float] [duration(seconds):Int]")
      }
      case "animation" => args match {
          case Seq(a:Int,d:Int, xs @ _ *) => dance(a,d)
          case Seq("help", xs @ _ *) => println( """
              command: animation [pattern:Int] [duration(seconds):Int]
              patterns:
                PHI_M30_DEG(0)
                PHI_30_DEG(1)
                THETA_M30_DEG(2)
                THETA_30_DEG(3)
                THETA_20DEG_YAW_200DEG(4)
                THETA_20DEG_YAW_M200DEG(5)
                TURNAROUND(6)
                TURNAROUND_GODOWN(7)
                YAW_SHAKE(8)
                YAW_DANCE(9)
                PHI_DANCE(10)
                THETA_DANCE(11)
                VZ_DANCE(12)
                WAVE(13)
                PHI_THETA_MIXED(14)
                DOUBLE_PHI_THETA_MIXED(15)
                Flip Forward(16) (2.0 only)
                Flip Back(17) (2.0 only)
                Flip Left(18) (2.0 only)
                Flip Right(19) (2.0 only)
                ANIM_MAYDAY(20)
            """)

          case _ => println("command: animation [pattern:Int] [duration(seconds):Int]")
      }
      case _ => println( s"unknown command '$com' called with ${args.mkString("'", "', '", "'")}")
    }
    
  }


  override def config(key:String, value:Any){
    key match{
      case "help" => println("""
          config keys:

            maxEulerAngle     [Float](0 - .52 radians)
            maxVerticalSpeed  [Float](0.2 - 2.0 m/s)
            maxRotationSpeed  [Float](.7 - 6.11 rad/s)

            videoMode         [Int](0 - 3)

        """)
      case "maxEulerAngle" if value.isInstanceOf[Float] => setMaxEuler(value.asInstanceOf[Float])
      case "maxVerticalSpeed" if value.isInstanceOf[Float] => setMaxVertical(value.asInstanceOf[Float])
      case "maxRotationSpeed" if value.isInstanceOf[Float] => setMaxRotation(value.asInstanceOf[Float])
      case "videoMode" if value.isInstanceOf[Int] => videoStream.foreach( _.config("mode",value.asInstanceOf[Int]))

      case "ip" if value.isInstanceOf[String] => ip = value.asInstanceOf[String] 

      case _ => println(s"unknown config key '$key' for value $value")
    }
  }

  // helper methods

  def clearEmergency(){
    if( drone.isEmpty ){
      println("Drone not connected.")
      return
    }
    drone.get.clearEmergencySignal
  }

  def trim() = drone.foreach( _.trim )
    
  def toggleFly{
    if( drone.isEmpty ){
      println("Drone not connected.")
      return
    }
    if( sensors("flying").bool ) land
    else takeOff
  }

  def playLed(anim:Int, freq:Float, dur:Int){
    if( drone.isEmpty ){
      println("Drone not connected.")
      return
    }
    drone.get.playLED(anim, freq, dur)
  }

  def dance(anim:Int, dur:Int){
    if( drone.isEmpty ){
      println("Drone not connected.")
      return
    }
    drone.get.playAnimation(anim, dur)
  } 

  // used to set ARDrone config options
  def setConfigOption(name:String,value:String){
    drone.foreach( _.setConfigOption(name,value) )
  }
  def setMaxEuler(v:Float){
    maxEuler = v
    setConfigOption("control:euler_angle_max",v.toString)
  }
  def setMaxVertical(v:Float){
    maxVert = v
    val config = (v*1000.f).toInt
    setConfigOption("control:control_vz_max",config.toString)
  } 
  def setMaxRotation(v:Float){
    maxRot = v
    setConfigOption("control:control_yaw",v.toString)
  }

  def getVersion():String = {
    if( drone.isEmpty ) return null
    val v = drone.get.getDroneVersion()
    println("version: " + v )
    v
  }

  // telnet commands
  def reboot() = {
    val t = new Thread(){
      override def run(){
        try{
          println("Sending reboot...")
          if( drone.isDefined ) disconnect
          val c = new SimpleTelnetClient(ip)
          c.send("reboot")
          c.disconnect
          println("Rebooting ARDrone. ")
        } catch {
          case e: Exception => println("Reboot failed.") 
        }
      }
    }
    t.start
  }

  def telnet(command:String) = {
    val t = new Thread(){
      override def run(){
        println("Sending telnet command: " + command)
        val c = new SimpleTelnetClient(ip)
        c.send(command)
        c.disconnect
      }
    }
    t.start
  }
}

class ARDroneSensorData( val drone:JD ) extends SensorData with NavDataListener {

  // ARDrone state internal
  var gyroAngles = Vec3()
  var estimatedVelocity = Vec3()

  set( Velocity(estimatedVelocity) )
  set( Gyroscope(gyroAngles) )
  set(Altimeter(0))
  set(Battery(0))
  set(Sensor("emergency",false))
  set(Sensor("flying",false))


   // drone navdata and video callbacks
  def navDataReceived(nd:NavData){
    gyroAngles.x = nd.getPitch
    gyroAngles.z = nd.getRoll
    gyroAngles.y = nd.getYaw
    estimatedVelocity.x = nd.getVx
    estimatedVelocity.y = nd.getLongitude
    estimatedVelocity.z = nd.getVz

    set( Velocity(estimatedVelocity) )
    set( Gyroscope(gyroAngles) )
    set(Altimeter(nd.getAltitude))
    set(Battery(nd.getBattery))
    set(Sensor("emergency",drone.isEmergencyMode))
    set(Sensor("flying",nd.isFlying))
  }
   
}

class ARDroneVideoStream( val drone:JD ) extends VideoStream with DroneVideoListener {

  // holds last video frame
  var frame: BufferedImage = _

  override def start(){
    drone.enableVideo()
    drone.addImageListener(this)
  }
  override def stop(){
    drone.disableVideo()
    drone.removeImageListener(this)
  }
  def getFrame() = frame

  override def config(key:String, value:Any){
    try{
      key match {
        case "mode" if value.isInstanceOf[Int] => selectCameraMode(value.asInstanceOf[Int])
        case _ => println(s"Unhandled config key: $key for value $value")
      }
    } catch { case e:Exception => println(e) }
  }

  // called when video frame received
  def frameReceived(startX:Int, startY:Int, w:Int, h:Int, rgbArray:Array[Int], offset:Int, scansize:Int){
    if( frame == null || frame.getWidth != w || frame.getHeight != h ) frame = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
    frame.setRGB(startX, startY, w, h, rgbArray, offset, scansize)
  }
  def frameReceived(bi:BufferedImage){
    frame = bi;
  }


  def selectCameraMode( mode: Int){
    var m = mode % 4
    m match {
      case 0 => drone.selectVideoChannel( JD.VideoChannel.HORIZONTAL_ONLY )
      case 1 => drone.selectVideoChannel( JD.VideoChannel.VERTICAL_ONLY )
      case 2 => drone.selectVideoChannel( JD.VideoChannel.HORIZONTAL_IN_VERTICAL )
      case 3 => drone.selectVideoChannel( JD.VideoChannel.VERTICAL_IN_HORIZONTAL )
      case _ => println(s"$m is not a valid video mode, try 0 - 3")
    }
  }
}