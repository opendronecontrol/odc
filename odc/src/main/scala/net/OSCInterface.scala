
package org.opendronecontrol
package net

import drone._
import tracking._
import spatial._

import de.sciss.osc._
import Implicits._

import scala.collection.mutable.Map

// Mixins unecessary for now- remove
// /** OSCInterface Module
//   *   this is a mixin trait for DroneBase that listens for
//   *   commands over the network via the OpenSoundControl Protocol
//   */
// trait OSCMixin extends DroneBase{
//   val osc = new OSCInterface(this)
// }

/** OSCInterface
  * Implements drone related OSC message handling.
  *  
  * This can be started by running:
  * {{{
  *    val drone = new ARDrone
  *    drone.osc.start(8000) // listen for messages on port 8000
  * }}}
  *
  *  Example Messages:
  *  {{{
  *  /connect
  *  /sendSensors "192.168.1.255" 8001
  *  /takeOff
  *  /move 0.0 0.0 0.0 0.5
  *  /config "maxEulerAngle" 0.2
  *  /config "maxVerticalSpeed" 1.0
  *  /led [pattern:Int] [frequency:Float] [duration(seconds):Int]
  *  /animation [pattern:Int] [duration(seconds):Int]
  *  /land
  *  /disconnect
  *  }}}
  */
class OSCInterface(val drone:DroneBase) {

  var dump = false
  val cfg = UDP.Config()
  var rcv = UDP.Receiver(cfg) 
  var port = 8000

  val ccfg = UDP.Config()  
  ccfg.codec = PacketCodec().doublesAsFloats().booleansAsInts()
  var out = UDP.Client( localhost -> 8001, ccfg )

  def start(port:Int=8000){

    this.port = port

    println( s"Listening for drone commands on port $port" )
    println( "  Example Commands: ")
    println( "    /connect")
    println( "    /sendSensors 192.168.1.255 8001")
    println( "    /takeOff")
    println( "    /move 0.1 0.0 0.0 0.0")
    println( "    /land")
    println( "    /disconnect")

    cfg.localPort = port
    rcv = UDP.Receiver( cfg )

    if( dump ) rcv.dump( Dump.Both )

    rcv.action = {

      case (b:Bundle, _) => handleBundle(b)
      case (m:Message, _) => handleMessage(m)
       
      case (p, addr) => println( "Ignoring: " + p + " from " + addr )
    }

    rcv.connect()
  }

  def handleBundle(bundle:Bundle){
    bundle match {
      case Bundle(t, msgs @ _*) =>
        msgs.foreach{
          case b:Bundle => handleBundle(b)
          case m:Message => handleMessage(m)
          case _ => println("unhandled object in bundle")
        }
      case _ => println("bad bundle")
    }
  }

  def handleMessage(msg:Message){
    msg match {
      case Message("/connect") => drone.connect
      case Message("/disconnect") => drone.disconnect
      case Message("/reset") => drone.reset
      case Message("/takeOff") => drone.takeOff
      case Message("/land") => drone.land
      case Message("/move",x:Float,y:Float,z:Float,r:Float) => drone.move(x,y,z,r)
      case Message("/left",x:Float) => drone.left(x)
      case Message("/right",x:Float) => drone.right(x)
      case Message("/forward",x:Float) => drone.forward(x)
      case Message("/back",x:Float) => drone.back(x)
      case Message("/up",x:Float) => drone.up(x)
      case Message("/down",x:Float) => drone.down(x)
      case Message("/ccw",x:Float) => drone.ccw(x)
      case Message("/cw",x:Float) => drone.cw(x)
      case Message("/hover") => drone.hover


      case Message("/moveTo",a:Float,b:Float,c:Float,d:Float) => drone.tracking.moveTo(a,b,c,d)
      case Message("/step",a:Float,b:Float,c:Float,d:Float) => drone.tracking.step(a,b,c,d)
      

      case Message("/sendSensors", ip:String, port:Int) => sendSensors(ip,port)
      case Message("/broadcastSensors", port:Int) => broadcastSensors(port)

      case Message("/config", key:String, value:Any) => drone.config(key,value)

      case Message( name, vals @ _* ) => drone.command(name.replaceFirst("/",""), vals:_* )
      case _ => println( "Ignoring: " + msg )
    }
  }

  def stop() = { println("OSC server shutting down.."); drone.sensorData.foreach(_.unbind); rcv.close(); out.close(); }

  def broadcastSensors(port:Int=8001) = sendSensors("255.255.255.255",port)

  def sendSensors(ip:String="localhost", port:Int=8001){
    if( !drone.hasSensors() ){
      println("Drone has no available sensor data, make sure you are properly connected..")
      return
    }

    out.close
    out = UDP.Client( ip -> port, ccfg )
    out.channel.socket.setBroadcast(true)
    out.connect       

    drone.sensors.bind( (s) => {
      val name = s.name
      val path = s"/$name"
      try{
        s.value match {
          case v:Float => out ! Message(path,v)
          case v:Int => out ! Message(path,v)
          case v:Boolean => out ! Message(path,v)
          case v:Vec3 => out ! Message(path,v.x,v.y,v.z)
          case v:Quat => out ! Message(path,v.w,v.x,v.y,v.z)
          case _ => ()
        }
      } catch {
        case e:Exception => println(e)
      }
    })

  }

}



