/*
///    [ODC] Processing Example - DroneoscP5 - Tim Wood 2013 - fishuyo@gmail.com || http://fishuyo.com/  
///    Control ARDrone using oscP5
///    Connects to a OpenDroneControl client that is listening for OSC messages.
///    This can be used to allow multiple connections / sketches communicating 
///    with the same drone. 
///
///    Press 'u' to takeoff and land
///
///    if you've downloaded this code from the ODC github you may be missing the ODC processing libraries, please visit the ODC website to download them
///    http://www.opendronecontrol.org/
*/

import oscP5.*;
import netP5.*;
  
OscP5 oscP5;
NetAddress address;

String ip = "127.0.0.1"; /* ip address of ARDrone ODC client listening for osc messages */
int outPort = 8000; /* port of ODC client */
int recvPort = 8001; /* port to receive sensor data on */

void setup() {
  size(400,400);
  frameRate(25);
  /* start oscP5, listening for incoming messages at recvPort */
  oscP5 = new OscP5(this,recvPort);
  address = new NetAddress(ip, outPort); 
  
  OscMessage myMessage = new OscMessage("/connect");
  oscP5.send(myMessage, address); 
  
  myMessage = new OscMessage("/broadcastSensors");
  myMessage.add(recvPort); /* add recvPort to the osc message */
  oscP5.send(myMessage, address); 
}


void draw() {
  background(0);  
}

void mousePressed() {

}

/* incoming osc message are forwarded to the oscEvent method. */
void oscEvent(OscMessage theOscMessage) {
  
  /* print the gyroscope data */
  if(theOscMessage.checkAddrPattern("/gyroscope") == true) {
    /* check if the typetag is the right one. */
    if(theOscMessage.checkTypetag("fff")) {
      /* parse theOscMessage and extract the values from the osc message arguments. */
      float x = theOscMessage.get(0).floatValue();  
      float y = theOscMessage.get(1).floatValue();
      float z = theOscMessage.get(2).floatValue();
      println(" gyroscope: "+x+", "+y+", "+z);
      return;
    }  
  }
}


void keyPressed(){
  if (key =='u'){
    if(flying==false){
      myMessage = new OscMessage("/takeOff");
      oscP5.send(myMessage, address);
    } else{
      myMessage = new OscMessage("/land");
      oscP5.send(myMessage, address);
    }
  }
}
