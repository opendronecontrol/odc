
package org.opendronecontrol
package examples

import spatial._
import platforms.ardrone._

object TakeoffMoveLand extends App {

  val drone = new ARDrone
  drone.connect()  
  Thread.sleep(1000)

  drone.takeOff()
  Thread.sleep(5000) // wait for drone to takeoff and stabalize

  var t = 0      // keep track of approximate total time
  var dt = 30    // time per iteration of control loop

  var period = 3000 // movement oscillation period

  // control loop
  //  for smooth flight move commands should be sent at a consistent interval of 30ms
  while( t < 10000){

    var phase = (t % period) / period.toFloat * (2*math.Pi)

    var rot = math.sin(phase).toFloat

    drone.move(0.0f, 0.0f, 0.0f, rot) // oscillate drone left/right
    
    // print out some sensor data
    if( drone.hasSensors() ){
      println( drone.sensors("velocity").vec )
      println( drone.sensors("gyroscope").vec )
      println( drone.sensors("altimeter").float )
      println( drone.sensors("battery").int )
    }

    t += dt
    Thread.sleep(dt)
  }


  drone.land()  

  drone.disconnect()

  System.exit(0)

}