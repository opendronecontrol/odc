
package org.opendronecontrol
package examples

import spatial._
import platforms.ardrone._
import drone.sensors._

object GetSensorData extends App {

  val drone = new ARDrone
  drone.connect()    

  // the sensors object is made when drone connects

  // option 1
  drone.sensors.bind(  (s) => {
    println( s.name + ": " + s.value )
    //println( s" ${s.name} : ${s.value}" ) // string interpolation
  })
  Thread.sleep(5000)
  

  // option 2
  // var t = 0

  // while( t < 20000){    
  //   Thread.sleep(30)
  //   if( drone.hasSensors() ){
  //     println( "velocity: " + drone.sensors("velocity").value )
  //     println( "gyroscope: " + drone.sensors("gyroscope").value )
  //     println( "altimeter: " + drone.sensors("altimeter").value )
  //     println( "battery: " + drone.sensors("battery").value )
  //   }
  //   t += 30
  // }

  drone.disconnect()

  System.exit(0)

}