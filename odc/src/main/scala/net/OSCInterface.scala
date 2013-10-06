
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
// trait OSCInterface extends DroneBase{
// 	val osc = new OSCServer(this)
// }

// /** OSCInterfaceTrack Module
//   * This is a mixin trait for DroneBase that listens for
//   * commands over the network via the OpenSoundControl Protocol.
//   * Also handles messages to [[org.opendronecontrol.tracking.PositionController]] module
//   */
// trait OSCInterfaceTrack extends PositionController {
// 	val osc = new OSCServer(this, this.tracker)
// }



/** OSCInterface
	* Implements drone related OSC message handling.
	*  
	* This can be started by running:
	* {{{
	*		val drone = new ARDrone
	*		drone.osc.start(8000) // listen for messages on port 8000
	* }}}
	*
	*	Example Messages:
	*	{{{
	*	/connect
	*	/sendSensors "192.168.1.255" 8001
	*	/takeOff
	*	/move 0.0 0.0 0.0 0.5
	*	/config "maxEulerAngle" 0.2
	*	/config "maxVerticalSpeed" 1.0
	*	/led [pattern:Int] [frequency:Float] [duration(seconds):Int]
	*	/animation [pattern:Int] [duration(seconds):Int]
	*	/land
	*	/disconnect
	*	}}}
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
		println( "	Example Commands: ")
		println( "		/connect")
		println( "		/sendSensors 192.168.1.255 8001")
		println( "		/takeOff")
		println( "		/move 0.1 0.0 0.0 0.0")
		println( "		/land")
		println( "		/disconnect")

	  cfg.localPort = port
	  rcv = UDP.Receiver( cfg )

	  if( dump ) rcv.dump( Dump.Both )

	  rcv.action = {

	  	case (Message("/connect"), _) => drone.connect
	  	case (Message("/disconnect"), _) => drone.disconnect
	  	case (Message("/reset"), _) => drone.reset
	  	case (Message("/takeOff"), _) => drone.takeOff
	  	case (Message("/land"), _) => drone.land
	  	case (Message("/move",x:Float,y:Float,z:Float,r:Float), _) => drone.move(x,y,z,r)
	  	case (Message("/hover"), _) => drone.hover


	  	case (Message("/moveTo",a:Float,b:Float,c:Float,d:Float), _) => drone.tracking.moveTo(a,b,c,d)
	  	

	  	case (Message("/sendSensors", ip:String, port:Int), _) => sendSensors(ip,port)
	  	case (Message("/broadcastSensors", port:Int), _) => broadcastSensors(port)

	  	case (Message("/config", key:String, value:Any), _) => drone.config(key,value)

	    case (Message( name, vals @ _* ), _) =>
	    	drone.command(name.replaceFirst("/",""), vals:_* )
	     
	    case (p, addr) => println( "Ignoring: " + p + " from " + addr )
	  }

	  rcv.connect()
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



