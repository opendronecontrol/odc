
package org.opendronecontrol
package apps
package droneOSC

import drone._
import tracking._
import spatial._
import net._
import platforms.ardrone._

object Main extends App {

  var drone = new ARDrone with PositionController with OSCInterfaceTrack
  // drone.osc.dump = true
  drone.osc.start()


  // Test sensor broadcasting without Drone
  // var f = 0.f
  // drone.sensorData = Some(new SensorData())
  
  // while(true){
  // 	Thread.sleep(30)
  // 	f += .00001f
  // 	val sensors = drone.sensorData.get
  // 	sensors.set( Accelerometer(Vec3(f)))
  // 	sensors.set( Gyroscope(Vec3(f*2.f)))
  // 	sensors.set( Battery(f.toInt) )
  // }
}




