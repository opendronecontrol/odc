
package org.opendronecontrol
package apps
package droneOSC

import spatial._
import platforms.ardrone._

import de.sciss.osc._

import scala.collection.mutable.Map


object Main extends App {

  var drone = new ARDrone

  // val live = new Ruby("droneosc.rb")

  DroneOSC.dump = true
  DroneOSC.listen()

}

object DroneOSC{

	var dump = false
	val sync = new AnyRef
	var callbacks = Map[String,(Float*)=>Unit]()
	//var callbacks2 = Map[String,(Float,Float)=>Unit]()

	def clear() = callbacks.clear()
	def bind( s:String, f:(Float*)=>Unit) = callbacks += s -> f

	def listen(port:Int=8000){
		import Main.drone

		val cfg         = UDP.Config()
	  cfg.localPort   = port  // 0x53 0x4F or 'SO'
	  val rcv         = UDP.Receiver( cfg )

	  def f(s:String)(v:Float*) = {println(s)}

	  if( dump ) rcv.dump( Dump.Both )
	  rcv.action = {
	  	case (Message("/connect", ip:String), _) => drone.ip = ip; drone.connect()
	  	case (Message("/disconnect"), _) => drone.disconnect
	  	case (Message("/takeOff"), _) => drone.takeOff
	  	case (Message("/land"), _) => drone.land
	  	case (Message("/move",x:Float,y:Float,z:Float,r:Float), _) => drone.move(x,y,z,r)
	  	// case (Message("/moveTo",a:Float,b:Float,c:Float,d:Float), _) => drone.moveTo(a,b,c,d)

	  	case (Message("/led",a:Int, f:Float, d:Int), _) => drone.playLed(a,f,d)

	  	case (Message("/quit"), _) => println("quit.."); rcv.close(); sys.exit(0);
	    // case (Message( name, vals @ _* ), _) =>
	    //   callbacks.getOrElse(name, f(name)_ )(vals.asInstanceOf[Seq[Float]]:_*)
	     
	    case (p, addr) => println( "Ignoring: " + p + " from " + addr )
	  }
	  rcv.connect()
	}
}



