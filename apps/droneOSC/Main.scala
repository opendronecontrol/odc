
package org.opendronecontrol
package apps
package droneOSC

import drone._
import tracking._
import spatial._
import net._
import platforms.ardrone._

// import de.sciss.osc._

// import scala.collection.mutable.Map


object Main extends App {

  var drone = new ARDrone with PositionController with OSCInterface
  // drone.osc.dump = true
  drone.osc.start()

  var f = 0.f

  drone.sensorData = Some(new SensorData())

  // Test sensor broadcasting without Drone
  //
  // while(true){
  // 	Thread.sleep(30)
  // 	f += .00001f
  // 	val sensors = drone.sensorData.get
  // 	sensors.set( Accelerometer(Vec3(f)))
  // 	sensors.set( Gyroscope(Vec3(f*2.f)))
  // 	sensors.set( Battery(f.toInt) )
  // }
}




