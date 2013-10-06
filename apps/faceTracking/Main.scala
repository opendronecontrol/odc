
package org.opendronecontrol
package apps
package faceTracking

import platforms.ardrone._

import com.fishuyo._
import graphics._
import dynamic._

import cv._

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.glutils._

import org.opencv.core._
import org.opencv.highgui._
import org.opencv.imgproc._

import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

object Main extends App with GLAnimatable{

  SimpleAppRun.loadLibs()
  System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME)
  GLScene.push(this)

  // ODC ARDrone platform
  val drone = new ARDrone("192.168.1.1")

  // quad to render video stream on
  val quad = Model(Quad())

  var frame:BufferedImage = _
  var texData:FloatBuffer = _
  var pix:Pixmap = _
  var bytes:Array[Byte] = _
  var texID = 0
  var (w,h) = (0,0)

  implicit var camera = new CalibratedCamera()
  var faceDetector = new FaceDetector(0.17/2.0)

  // Ruby script runner - reloads on save
  val live = new Ruby("face.rb")

  // Run the app
  SimpleAppRun() 


  /* member methods */

  override def draw(){

    if(drone.hasVideo()) frame = drone.video.getFrame()
    
    if( frame != null){
      // allocate texture if frame size changed or first frame
      if( w == 0 || w != frame.getWidth || h != frame.getHeight){
        w = frame.getWidth
        h = frame.getHeight

        // scale quad to correct aspect ratio
        quad.scale.set(1.f, -(h/w.toFloat), 1.f)

        // allocate texture and byte buffer
        pix = new Pixmap(w,h, Pixmap.Format.RGB888)
        bytes = new Array[Byte](h*w*3)
        texID = Texture(pix) 

      }

      val data = frame.getRGB(0,0,w,h,null,0,w)
      for( i <- (0 until data.length)){
        val c = data(i)
        bytes(3*i) = (c >> 16 & 0xFF).toByte 
        bytes(3*i+1) = (c >> 8 & 0xFF).toByte
        bytes(3*i+2) = (c & 0xFF).toByte
      }
      val img = new Mat(h,w,CvType.CV_8UC3)
      img.put(0,0,bytes)

      val small = new Mat()
      // scale image run faster
      Imgproc.resize(img,small, new Size(), 0.5,0.5,0)
      val count = faceDetector(small)

      val bb = pix.getPixels()
      bb.put(bytes)
      bb.rewind()

      if( count > 0){
        // get face position and draw scaling back up to match full size frame
        val x = faceDetector.face.x*2
        val y = faceDetector.face.y*2
        val w = faceDetector.face.width*2
        val h = faceDetector.face.height*2
        pix.setColor(0.f,1.f,0.f,1.f)
        pix.drawRectangle(x,y,w,h)
      }

      Texture(texID).draw(pix,0,0)


      // bind and update texture on gpu
      Texture.bind(texID)
      Shader.texture = 1.f
      Shader.lighting = 0.f
    }

    quad.draw()
  }
  override def step(dt:Float){
    live.step(dt)
  } 

}






