
package org.opendronecontrol
package apps
package leapController

import platforms.ardrone._

import com.fishuyo._
import graphics._
import dynamic._

import java.awt.image.BufferedImage
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

object Main extends App with GLAnimatable{

  SimpleAppRun.loadLibs()
  GLScene.push(this)

  // ODC ARDrone platform
  val drone = new ARDrone("192.168.1.1")

  // quad to render video stream on
  val quad = Quad()

  var frame:BufferedImage = _
  var texData:FloatBuffer = _
  var texID = 0
  var (w,h) = (0,0)

  // Ruby script runner - reloads on save
  val live = new Ruby("leap.rb")

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

        // allocate texture data as float buffer
        val buffer = ByteBuffer.allocateDirect(w*h*4 * 4);
        buffer.order(ByteOrder.nativeOrder());
        texData = buffer.asFloatBuffer();
        texID = Texture(w,h,texData)
      }

      // set texture data from frame
      val data = new Array[Int](w*h*4)
      val fdata = new Array[Float](w*h*4)
      frame.getRGB(0,0,w,h,data,0,w)
      for( i <- (0 until data.length)){
        fdata(i) = data(i) / 255.f
      }
      texData.put(fdata)
      texData.rewind

      // bind and update texture on gpu
      Texture.bind(texID)
      Texture(texID).getTextureData().consumeCompressedData()
      Shader.texture = 1.f
    }

    quad.draw()
  }
  override def step(dt:Float){
    live.step(dt)
  } 

}






