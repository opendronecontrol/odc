
package org.opendronecontrol.net

import java.io._
import java.net._

class SimpleTelnetClient( val ip:String="192.168.1.1", var port:Int=23 ) {

  val socket = new Socket(ip,port)
  socket.setKeepAlive(true)

  val r = new BufferedReader(new InputStreamReader(socket.getInputStream()))
  val w = new PrintWriter(socket.getOutputStream())

  //read

  def send( m:String ) = {
    w.print(m+"\r\n")
    w.flush()
  }

  def read = while(r.ready) print(r.read.toChar)

  def disconnect = socket.close()
}
