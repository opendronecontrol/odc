

package org.opendronecontrol
package platforms.ardrone

import spatial._
import drone._
import net.SimpleTelnetClient

import com.codeminders.ardrone.{ARDrone => JD}
import com.codeminders.ardrone._

import java.net._
import java.awt.image.BufferedImage

import scala.collection.mutable.Queue

class ARDrone(var ip:String="192.168.1.1") extends DroneBase with NavDataListener with DroneVideoListener {

  // current state flags
  var (connecting, ready, flying) = (false,false,false)

  // javadrone api ARDrone
  var drone : JD = _

  // holds last video frame
  var frame: BufferedImage = _
	
  // controller params
  var maxEuler = 0.3f  //(0 - .52 rad)
  var maxVert = 1.f   //(0.2 - 2. m/s)
  var maxRot = 3.0f    //(.7 - 6.11 rad/s)

  // ARDrone state internal
  var gyroAngles = Vec3()
  var estimatedVelocity = Vec3() //from accelerometer?
  var altitude = 0.f
  var battery = 0
  var emergency = false
  var nd:NavData = _


  /* DroneBase methods */

  def connect(){
    if( drone != null){
      println("Drone already connected.")
      return
    }else if( connecting ){
      return
    }
    connecting = true
    val _this = this
    val t = new Thread(){
      override def run(){
        try {
          val d = new JD( InetAddress.getByName(ip), 1000, 1000 )
         	println("connecting to ARDrone at " + ip + " ..." )
          d.connect
          d.clearEmergencySignal
          d.waitForReady(3000)
          println("ARDrone connected and ready!")
          d.trim
          d.addImageListener(_this)
          d.addNavDataListener(_this)
          drone = d
          connecting = false
          ready = true

        } catch {
          case e: Exception => println("Drone connection failed."); e.printStackTrace 
          connecting = false
        }  
      }
    }
    t.start
  }

  def disconnect(){
    if( drone == null){
      println("Drone not connected.")
      return
    }
    if( flying ) drone.land
    drone.disconnect
    drone = null
    ready = false
    println("Drone disconnected.") 
  }

  override def reset(){
    clearEmergency()
  }

  def takeOff(){
    if( drone == null){
      println("Drone not connected.")
      return
    }
    drone.takeOff
  }

  def land(){
    if( drone == null){
      println("Drone not connected.")
      return
    }
    drone.land
  }

  def move( x: Float, y: Float, z: Float, r: Float ){
    if( drone == null){
      println("Drone not connected.")
      return
    }
    drone.move(x,z,y,r) 
  }

  override def hover(){
    if( drone == null){
      println("Drone not connected.")
      return
    }
    drone.hover
  }


  /* ARDrone helper methods */

  def clearEmergency(){
    if( drone == null){
      println("Drone not connected.")
      return
    }
    drone.clearEmergencySignal
  }

  def trim() = drone.trim
    
  def toggleFly = {
    if( flying ) land
    else takeOff
  }

  def playLed(anim:Int, freq:Float, dur:Int){
    if( drone == null){
      println("Drone not connected.")
      return
    }
    drone.playLED(anim, freq, dur)
  }

  def dance(anim:Int, dur:Int){
    if( drone == null){
      println("Drone not connected.")
      return
    }
    drone.playAnimation(anim, dur)
  } 

  // used to set ARDrone config options
  def setConfigOption(name:String,value:String){
    drone.setConfigOption(name,value)
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

  def getVersion() = { val v = drone.getDroneVersion(); println("version: " + v ); v }

  def debug(){
    println( "ready: " + ready)
    println("flying: " + flying)
    //println("internal vel: " + vx + " " + vy + " " + vz)
    //println("move: " + tilt.x + " " + tilt.y + " " + ud + " " + r)
    //println("pitch roll yaw: " + pitch + " " + roll + " " + yaww)
    println("altitude: " + altitude)
    println("battery: " + battery)
    //if( drone != null ) qSize = drone.queueSize
    //println("command queue size: " + qSize)
  }

  // drone navdata and video callbacks
  def navDataReceived(nd:NavData){
    flying = nd.isFlying
    altitude = nd.getAltitude
    battery = nd.getBattery
    gyroAngles.x = nd.getPitch
    gyroAngles.z = nd.getRoll
    gyroAngles.y = nd.getYaw
    estimatedVelocity.x = nd.getVx
    estimatedVelocity.y = nd.getLongitude
    estimatedVelocity.z = nd.getVz
    emergency = drone.isEmergencyMode()
  }

  // called when video frame received
  def frameReceived(startX:Int, startY:Int, w:Int, h:Int, rgbArray:Array[Int], offset:Int, scansize:Int){
    if( frame == null || frame.getWidth != w || frame.getHeight != h ) frame = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
    frame.setRGB(startX, startY, w, h, rgbArray, offset, scansize)
  }

  def selectCameraMode( mode: Int){
    var m = mode % 4
    m match {
      case 0 => drone.selectVideoChannel( JD.VideoChannel.HORIZONTAL_ONLY )
      case 1 => drone.selectVideoChannel( JD.VideoChannel.VERTICAL_ONLY )
      case 2 => drone.selectVideoChannel( JD.VideoChannel.HORIZONTAL_IN_VERTICAL )
      case 3 => drone.selectVideoChannel( JD.VideoChannel.VERTICAL_IN_HORIZONTAL )
    }
  }

  // telnet commands
  def reboot() = {
    val t = new Thread(){
      override def run(){
        try{
          println("Sending reboot...")
          if( drone != null ) disconnect
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