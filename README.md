[ODC]
===

OpenDroneControl [ODC] is an open source software platform for developing interactive artworks and research projects with aerial robotics. http://opendronecontrol.org


Currently [ODC] is a first attempt at creating a generalized, open, extensible Drone API with a few extra benefits:

* Simple OpenSoundControl Interface - send control messages and receive sensor data over the network
* Built in Tracking support - adds ability to move drone to a coordinate in 3D space given a source of spatial tracking information (infrared camera system, computer vision,..)


[ODC] currently supports the ARDrone 1 and 2 platforms by utilizing an extension of [JavaDrone](https://code.google.com/p/javadrone/). Implementing additional platforms should be fairly straight forward although most likely require using a JNI bridge.


[ODC] comes with a [Max/MSP/Jitter](http://cycling74.com/products/max/) external object for ARDrone platform!
* download a template project (OSX) [here](http://opendronecontrol.org/downloads/ODC_MaxTemplate_osx.zip)
* download a template project (other platforms) [here](http://opendronecontrol.org/downloads/ODC_MaxTemplate_no_xuggle.zip)

[ODC] works in [Processing](http://processing.org/)
* download a template project [here](http://opendronecontrol.org/downloads/ODC_ProcessingTemplate.zip)

[ODC] is controllable from any device via OpenSoundControl!
* example app DroneOSC acts as a standalone ARDrone controller

[ODC] comes with a simple simulator program to test out flight paths and ideas without the hardware



Build
===

Build Max mxj external

```sbt
./sbt "project maxmsp-external" package
```

Run examples
```sbt
./sbt "project examples" run
```

Run DroneSimulatotr
```sbt
./sbt "project DroneSimulator" run
```


Example Usage
===

```scala
import org.opendronecontrol.platforms.ardrone._

val drone = new ARDrone("192.168.1.1")
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
```
Example using OSC interface

```scala
import org.opendronecontrol.platforms.ardrone._

val drone = new ARDrone
drone.osc.start(8000)
drone.osc.sendSensors("192.168.1.255", 8001)

```
The application is now listening for messages on port 8000 and broadcasting sensor data on port 8001.
Example messages:
```
	/connect
	/sendSensors "192.168.1.255" 8001
	/takeOff
	/move 0.0 0.0 0.0 0.5
	/config "maxEulerAngle" 0.2
	/config "maxVerticalSpeed" 1.0
	/led [pattern:Int] [frequency:Float] [duration(seconds):Int]
	/animation [pattern:Int] [duration(seconds):Int]
	/land
	/disconnect
```
