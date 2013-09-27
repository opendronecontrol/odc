
package org.opendronecontrol
package examples

import spatial._
import platforms.ardrone._
import drone.sensors._

object GetSensorData extends App {

  val drone = new ARDrone
  drone.connect()    

  // option 1
  // set a callback function called whenever a sensor is updated 
  drone.sensors.bind(  (s) => {
    println( s.name + ": " + s.value )
  })
  Thread.sleep(5000)
  

  // option 2
  // get sensor value on demand
  //
  // var t = 0
  // var dt = 30
  // while( t < 20000){             // loop for 20 seconds retreiving sensors every 30 ms   
  //   if( drone.hasSensors() ){
  //     println( "velocity: " + drone.sensors("velocity").value )
  //     println( "gyroscope: " + drone.sensors("gyroscope").value )
  //     println( "altimeter: " + drone.sensors("altimeter").value )
  //     println( "battery: " + drone.sensors("battery").value )
  //   }
  //   t += dt
  //   Thread.sleep(dt)
  // }

  drone.disconnect()

  System.exit(0)

}