
package org.opendronecontrol
package apps
package example

import spatial._
import platforms.ardrone._

object Main extends App {

  val drone = new ARDrone
  drone.connect()
  Thread.sleep(2000)

  drone.takeOff()
  Thread.sleep(3000)

  var t = 0
  var forward = true
  var step = 2000

  while( t < 20000){

    if(t % step >= 1000){
      forward = false
    } else if(t % step >= 0) {
      forward = true
    }

    if(forward) drone.forward( 0.1f)
    else drone.back( 0.1f)
    Thread.sleep(30)
    
    if( drone.hasSensors() ){
      println( drone.sensors("velocity").value )
      println( drone.sensors("gyroscope").value )
      println( drone.sensors("altimeter").value )
      println( drone.sensors("battery").value )
    }
    t += 30
  }

  drone.land()
  Thread.sleep(1000)

  drone.disconnect()

  System.exit(0)

}