
package org.opendronecontrol
package examples

import spatial._
import platforms.ardrone._

object TakeoffLand extends App {

  val drone = new ARDrone
  drone.connect()  
  Thread.sleep(1000) // sleep to make sure the drone is ready for commands after connecting
  
  drone.takeOff()
  Thread.sleep(10000)

  // hover for 10 seconds
  
  drone.land()

  drone.disconnect()

  System.exit(0)

}