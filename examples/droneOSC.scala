
package org.opendronecontrol
package examples

import spatial._
import platforms.ardrone._

object DroneOSC extends App {

  var port = 8000

  try{ 
    if( args.length >= 1) port = args(0).toInt
  } catch{
    case e:Exception => println( s"usage: droneOSC [port]")
  }

  // create a ARDrone client and start listening for OSC messages
  var drone = new ARDrone()  
  drone.osc.start(port)

  // wait for return then exit
  Console.readLine
  System.exit(0)
}




