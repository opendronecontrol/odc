
package org.opendronecontrol
package drone
package video

import java.awt.image.BufferedImage

trait VideoStream {

  def start(){}
  def stop(){}
  def apply() = getFrame()
  def getFrame():BufferedImage
  def config(key:String, value:Any){}

}