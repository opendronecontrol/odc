

package org.opendronecontrol
package drone

abstract class DroneBase {

  def connect()
  def disconnect()
  def reset(){}

  def takeOff()
  def land()
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
  

  def command(com:String){}
  def config(key:String, value:String){}

}

