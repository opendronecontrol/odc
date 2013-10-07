

package org.opendronecontrol
package drone

import sensors._
import video._
import net._
import tracking._

/** Abstract interface to a drone platform implementation.
  * 
  * Implemented in [[org.opendronecontrol.platforms.ardrone.ARDrone]] and [[org.opendronecontrol.drone.SimDrone]]
  */
abstract class DroneBase {

  def connect()
  def disconnect()
  def reset(){}

  def takeOff()
  def land()

  /** move the drone relative to its local right handed coordinate frame where +x is to its right and -z is forward
    * @param x pecentage of maximum acceleration in x direction 
    * @param y pecentage of maximum acceleration in y direction 
    * @param z pecentage of maximum acceleration in z direction 
    * @param r pecentage of maximum rotation negative r causes counter clockwise rotation  
    */
  def move(x:Float,y:Float,z:Float,r:Float)

  def hover(){}

  def forward(v:Float) = move(0,0,-v,0)
  def back(v:Float) = move(0,0,v,0)
  def left(v:Float) = move(-v,0,0,0)
  def right(v:Float) = move(v,0,0,0)
  def up(v:Float) = move(0,v,0,0)
  def down(v:Float) = move(0,-v,0,0)
  def cw(v:Float) = move(0,0,0,v)
  def ccw(v:Float) = move(0,0,0,-v)
  
  /** command function is to handle drone specific commands
    *   @param com command name to be executed
    *   @param args list of arguments for the command
    */
  def command(com:String, args:Any* ){}

  /** config function is to handle drone specific configuration parameters
    *   @param key configuration key
    *   @param value value to be assigned
    */
  def config(key:String, value:Any){}

  /** video stream should be implemented by extending [[org.opendronecontrol.drone.video.VideoStream]]
    *
    * {{{
    * class MyVideoStream extends VideoStream {...}
    * drone.video = Some(new MyVideoStream)
    * }}}
    */
  def hasVideo() = videoStream.isDefined
  def video = videoStream.get
  var videoStream:Option[VideoStream] = None

  /** sensor data should be implemented by extending [[org.opendronecontrol.drone.sensors.SensorData]]
    *
    * {{{
    * class MySensorData extends SensorData {...}
    * drone.sensorData = Some(new MySensorData)
    * }}}
    */
  def hasSensors() = sensorData.isDefined
  def sensors = sensorData.get
  var sensorData:Option[SensorData] = None


  /** osc interface module to control drone via network messages */
  val osc = new OSCInterface(this)

  /** tracking adds moveTo command given some regularlly updated spatial information  */
  val tracking = new PositionTrackingController(this)

}



