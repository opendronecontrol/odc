
package org.opendronecontrol
package apps
package leapController

import platforms.ardrone._

import com.fishuyo._
import graphics._
import dynamic._

object Main extends App with GLAnimatable{

  SimpleAppRun.loadLibs()
  GLScene.push(this)

  // ODC ARDrone platform
  val drone = new ARDrone("192.168.3.1")

  // quad to render video stream on
  val quad = Quad()

  // Ruby script runner - reloads on save
  val live = new Ruby("leap.rb")

  // Run the app
  SimpleAppRun() 


  /* member methods */

  override def draw(){
    quad.draw()
  }
  override def step(dt:Float){
    live.step(dt)
  } 

}






