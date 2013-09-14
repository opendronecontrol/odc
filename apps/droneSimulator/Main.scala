
package org.opendronecontrol
package apps
package sim

import drone._
import tracking._
import platforms.ardrone._

import com.fishuyo._
import maths._
import graphics._
import spatial._
import io._
import dynamic._
import audio._

import scala.collection.mutable.ListBuffer

object Main extends App with GLAnimatable{

  SimpleAppRun.loadLibs()
  GLScene.push(this)

  //simulation objects
  val simDrone = new SimDrone with PositionController
  val realDrone = new ARDrone("192.168.3.1") with PositionController
  val simBody = Primitive3D.cube(Pose(), Vec3(0.5f,.05f,.5f))
  val simControl = simDrone //new PositionTrackingController(simDrone)

  // Plots - 2d
  var plots = new ListBuffer[Plot2D]()
  var plotsFollowCam = false
  def togglePlotFollow() = plotsFollowCam = !plotsFollowCam
  
  plots += new Plot2D(100, 15.f)
  plots(0).pose.pos = Vec3(0.f, 2.f, 0.f)
  plots += new Plot2D(100, 15.f)
  plots(1).pose.pos = Vec3(0.f, 2.f, 0.f)
  plots(1).color = Vec3(2.f,0.f,0.f)

  plots += new Plot2D(100, 15.f)
  plots(2).pose.pos = Vec3(2.f, 2.f, 0.f)
  plots += new Plot2D(100, 15.f)
  plots(3).pose.pos = Vec3(2.f, 2.f, 0.f) 
  plots(3).color = Vec3(2.f,0.f,0.f)

  plots += new Plot2D(100, 15.f)
  plots(4).pose.pos = Vec3(4.f, 2.f, 0.f)
  plots += new Plot2D(100, 15.f)
  plots(5).pose.pos = Vec3(4.f, 2.f, 0.f)
  plots(5).color = Vec3(2.f,0.f,0.f)

  //Traces - 3d path data
  var traces = new ListBuffer[Trace3D]()
  traces += new Trace3D(100)
  traces += new Trace3D(100)
  traces += new Trace3D(100)
  traces += new Trace3D(100)

  //real drone
  // val realDroneBody = Primitive3D.cube(Pose(), Vec3(0.5f,.05f,.5f))
  // val control = new new PositionTrackingController(realDrone)

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

  override def draw(){
  	//realDroneBody.draw()
  	ground.draw()
  	moveCube.draw()
    simBody.draw()
  	plots.foreach( _.draw() )
  	traces.foreach( _.draw() )
  }
  override def step(dt:Float){
  	live.step(dt)
  	moveCube.pose.pos = simControl.tracker.destPose.pos

  	simDrone.step(dt)
    simBody.pose = simDrone.sPose
  } 

  implicit def pose2Pose( p:org.opendronecontrol.spatial.Pose ) : com.fishuyo.spatial.Pose = {
    Pose( Vec3(p.pos.x,p.pos.y,p.pos.z), Quat(p.quat.w,p.quat.x,p.quat.y,p.quat.z) )
  }
  implicit def vec2Vec( p:org.opendronecontrol.spatial.Vec3 ) : com.fishuyo.maths.Vec3 = {
    Vec3(p.x,p.y,p.z)
  }


}






