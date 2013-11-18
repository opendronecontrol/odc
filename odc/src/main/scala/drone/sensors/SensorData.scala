
package org.opendronecontrol
package drone
package sensors

import spatial._

import scala.collection.mutable.Map


/** Base Class for sensor data case classes
  */
class SensorBase[+T](val name:String, val value:T){
  def vec = value.asInstanceOf[Vec3]
  def getVec3 = value.asInstanceOf[Vec3]
  def float = value.asInstanceOf[Float]
  def getFloat = value.asInstanceOf[Float]
  def int = value.asInstanceOf[Int]
  def getInt = value.asInstanceOf[Int]
  def bool = value.asInstanceOf[Boolean]
  def getBoolean = value.asInstanceOf[Boolean]
}

/** Case class extension of [[SensorBase]]
  *
  * @param n sensor's name
  * @param v sensor's value
  */
case class Sensor[+T](n:String,v:T) extends SensorBase[T](n,v)

case class Accelerometer(v:Vec3) extends SensorBase[Vec3]("accelerometer",v)
case class Gyroscope(v:Vec3) extends SensorBase[Vec3]("gyroscope",v)

case class Velocity(v:Vec3) extends SensorBase[Vec3]("velocity",v)
case class Position(v:Vec3) extends SensorBase[Vec3]("position",v)

case class Altimeter(v:Float) extends SensorBase[Float]("altimeter",v)
case class Battery(v:Int) extends SensorBase[Int]("battery",v)


/** Holds map of all current Sensor readings
  *
  */
class SensorData {

  type Callback = (SensorBase[Any]) => Unit
  var callback = (s:SensorBase[Any]) => ()
  val callbacks = Map[String, Callback]()

  val sensorData = Map[String,SensorBase[Any]]()

  def hasSensor(name:String) = sensorData.keys.exists( _ == name.toLowerCase )
  def getSensorNames() = sensorData.keys
  def getSensorValues() = sensorData.values

  def listSensors() = this.getSensorNames().foreach( println(_) )

  def apply(name:String) = sensorData(name)

  def get(name:String) = this(name)
  def set(s:SensorBase[Any]) = {
    sensorData(s.name) = s
    callback(s)
  }

  def bind(f:Callback) = callback = f
  def unbind() = callback = (s:SensorBase[Any]) => ()
}