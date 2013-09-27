
package org.opendronecontrol
package examples

import spatial._
import platforms.ardrone._

object TakeoffMoveLand extends App {

  val drone = new ARDrone
  drone.connect()  
  Thread.sleep(1000)

  drone.takeOff()
  Thread.sleep(10000)

  var t = 0
  var forward = true
  var step = 2000

  while( t < 20000){

    if(t % step >= 1000){
      forward = false
    } else if(t % step >= 0) {
      forward = true
    }

    if(forward) drone.move(0.0f, 0.0f, -0.1f, 0.0f)
    else drone.move(0.0f, 0.0f, 0.1f, 0.0f)
    Thread.sleep(100)
    
    if( drone.hasSensors() ){
      println( drone.sensors("velocity").value )
      println( drone.sensors("gyroscope").value )
      println( drone.sensors("altimeter").value )
      println( drone.sensors("battery").value )
    }
    t += 100
  }

  drone.land()  

  drone.disconnect()

  System.exit(0)

}