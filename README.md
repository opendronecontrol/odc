[ODC]
===

OpenDroneControl [ODC] is an open source software platform for developing interactive artworks and research projects with aerial robotics. http://opendronecontrol.org


Currently [ODC] is a first attempt at creating a generalized, open, extensible Drone API with a few extra benefits:

* Simple OpenSoundControl Interface - send control messages and receive sensor data over the network
* Built in Tracking support - adds ability to move drone to a coordinate in 3D space


[ODC] currently supports the ARDrone 1 and 2 platforms by utilizing an extension of [JavaDrone](https://code.google.com/p/javadrone/). Implementing additional platforms should be fairly straight forward although most likely require using a JNI bridge.


[ODC] comes with a [Max/MSP/Jitter](http://cycling74.com/products/max/) external object for ARDrone platform!
* download a template project [here](http://opendronecontrol.org/downloads/templates/odc_max_template.zip)

[ODC] works in [Processing](http://processing.org/)
* download a template project [here](http://opendronecontrol.org/downloads/templates/odc_processing_template.zip)

[ODC] is controllable from any device via OpenSoundControl!
* example app droneOSC acts as a standalone ARDrone controller

[ODC] comes with a simple simulator program to test out flight paths and ideas without the hardware



Build
===

Build Max mxj external

```sbt
./sbt "project maxmsp-external" package
```

Run example Apps
```sbt
./sbt "project droneSimulator" run
./sbt "project droneOSC" run
```


Example Usage
===

```scala
import org.opendronecontrol.platforms.ardrone._

val drone = new ARDrone
drone.connect()
Thread.sleep(2000)

drone.takeOff()
Thread.sleep(2000)

var t = 0
while( t < 3000){
  drone.forward(0.5)
  Thread.sleep(200)
  drone.back(0.5)
  if( drone.hasSensorData() ){
    println( drone.sensors("velocity").value )
    println( drone.sensors("gyroscope").value )
    println( drone.sensors("altimeter").value )
    println( drone.sensors("battery").value )
  }
  t += 200
}

drone.land()
```
Example using OSC interface

```scala
import org.opendronecontrol.platforms.ardrone._
import org.opendronecontrol.net.OSCInterface

val drone = new ARDrone with OSCInterface
drone.osc.start(8000)
drone.osc.sendSensors("192.168.1.255", 8001)

```

