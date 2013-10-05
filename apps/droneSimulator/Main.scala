
package org.opendronecontrol
package apps
package sim

import drone._

import com.fishuyo._
import maths._
import graphics._
import spatial._
import io._
import dynamic._

import scala.collection.mutable.ListBuffer

object Main extends App with GLAnimatable{

  SimpleAppRun.loadLibs()
  GLScene.push(this)

  //simulation objects
  val simDrone = new SimDrone

  // start osc server to control virtual drone
  simDrone.osc.start(8000)

  val simBody = Primitive3D.cube(Pose(), Vec3(0.5f,.05f,.5f))
  simBody.color.set(RGB(0.f,0.6f,0.f))
    
  //Trace - 3d path data
  var trace = new Trace3D(400)

  // ground
  val ground = 	Primitive3D.cube(Pose(Vec3(0,-.03f,0),Quat()), Vec3(6,-.01f,6))
	ground.color.set(RGB(0.f,0.f,.6f))

  // moveTo cursor cube
  val moveCube = Primitive3D.cube(Pose(), Vec3(.05f))

  // Ruby script runner - reloads on save
  val live = new Ruby("sim.rb")

  // Run the app
  SimpleAppRun() 


  /* member methods */

  override def init(){ 
    Camera.nav.pos.set(0.1f,2.f,4.5f)
    Camera.nav.quat.set(1.f, -.13f,0.f,0.f)
  }
  override def draw(){
    Shader.lighting = 1.f
  	ground.draw()
  	moveCube.draw()
    simBody.draw()
    Shader.lighting = 0.f
  	trace.draw() 
  }
  override def step(dt:Float){
  	live.step(dt)

  	simDrone.step(dt)
    simBody.pose = simDrone.sPose
  } 

  // auto convert Pose to Pose
  implicit def pose2Pose( p:org.opendronecontrol.spatial.Pose ) : com.fishuyo.spatial.Pose = {
    Pose( Vec3(p.pos.x,p.pos.y,p.pos.z), Quat(p.quat.w,p.quat.x,p.quat.y,p.quat.z) )
  }
  implicit def vec2Vec( p:org.opendronecontrol.spatial.Vec3 ) : com.fishuyo.maths.Vec3 = {
    Vec3(p.x,p.y,p.z)
  }


}






