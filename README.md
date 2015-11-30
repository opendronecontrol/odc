OpenDroneControl [ODC]
===

[ODC is not under currently active development as of early 2014. If you would like to contribute to the project, contact us or submit a pull request.]

OpenDroneControl [ODC] is an open source software platform for developing interactive artworks and research projects with aerial robotics. ODC was developed to be a community-supported framework for connecting commercially available quadcopter platforms to a common programming interface. The framework provides access to platform specific sensors and optionally allows for additional functionality such as navigation and tracking.

ODC is compatible with creative coding software such as Processing, Max, and open Frameworks (oF). Additionally, ODC is designed for expansion through code modules, third party peripherals, integration with emerging aerial robotic platforms, and user developed applications. Application developers can utilize this common interface to easily target multiple drone platforms without redesigning their code which allows for rapid project development.
	
![](https://raw.githubusercontent.com/opendronecontrol/odc/master/images/odc_videostill.jpg "")
*A TouchOSC interface communicating with an ODC project in Max, and an AR-Drone.*

The development of this project was initiated by the Transvergent Research Group, including [Media Art and Technology](http://www.mat.ucsb.edu/) graduate student researchers [Tim Wood](http://www.fishuyo.com/), [Sterling Crispin](http://www.sterlingcrispin.com/), and [RJ Duran](http://rjduran.net). The technology was developed under the direction of Professor Marcos Novak, at the transLAB, housed within the California NanoSystems Institute (CNSI) at the University of California Santa Barbara. ODC originated from experiments within the transLAB’s motion tracking environment using a Parrot AR.Drone at the beginning of 2012. Initially the focus was to create an external Max object which could algorithmically control the drone using available spatial information provided by the OptiTrack system.

Feel free to fork and explore the github repo to add functionality and improve the platform.

To get started look at our notes from the [Drones & Aerial Robotics Workshop](http://opendronecontrol.org/darc-workshop/) hosted by ITP/NYU in October 2013.

Getting Started
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

DARC Workshop
===

Drones & Aerial Robotics Conference
====

October 11–13, 2013 | NYU/ITP | 721 Broadway, New York, NY 10003

We will be presenting the ODC framework at the Hackathon hosted by Nodecopter on Sunday, October 13th. Check out the [conference schedule](https://droneconference.org/schedule/) and [hackathon schedule](http://nodecopter.com/2013/new-york/oct-13). The hackathon is open for free registration here: http://www.eventbrite.com/event/8293334587.

Follow the conference and ODC at [@droneconference](https://twitter.com/droneconference), [@opendronectrl](https://twitter.com/opendronectrl).

[Download Handout](https://github.com/opendronecontrol/odc/raw/master/downloads/darc_workshop.pdf)