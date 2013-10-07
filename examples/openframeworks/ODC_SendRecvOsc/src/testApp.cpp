/*
 * OpenDroneControl [ODC] || http://www.opendronecontrol.org/
 * 
 * ODC_SendRecvOSC
 * RJ Duran || rjduranjr@gmail.com || http://rjduran.net || 2013
 * 
 * Description:
 * This example sets up a basic OSC interface for connecting to the ARDrone using DroneOSC.scala. 
 * Connect to the ARDrone over wifi. 
 * Run the file DroneOSC.scala by using ./sbt "project examples" run, then selecting the project.
 * This connects to the device. You can send OSC messages from your OF application to DroneOSC, which
 * gets passed onto the drone. 
 * 
 * Note: As of openFrameworks 0073 the project directory needs to be located under apps/myApps.
 */

#include "testApp.h"

//--------------------------------------------------------------
void testApp::setup(){

	ofBackground(30, 30, 130);

	// open an outgoing connection to HOST:PORT
	sender.setup(HOST, OUTPORT);
	receiver.setup(RECVPORT);
	
	current_msg_string = 0;
}

//--------------------------------------------------------------
void testApp::update(){
	// hide old messages
	for(int i = 0; i < NUM_MSG_STRINGS; i++){
		if(timers[i] < ofGetElapsedTimef()){
			msg_strings[i] = "";
		}
	}
	
	// check for waiting messages
	while(receiver.hasWaitingMessages()){
		// get the next message
		ofxOscMessage m;
		receiver.getNextMessage(&m);
		
		// check for gyroscope message
		if(m.getAddress() == "/gyroscope"){
			x = m.getArgAsFloat(0);
			y = m.getArgAsFloat(1);
			z = m.getArgAsFloat(2);			
		
		} else if(m.getAddress() == "/velocity") {
			v_x = m.getArgAsFloat(0);
			v_y = m.getArgAsFloat(0);
			v_z = m.getArgAsFloat(0);			

		} else if(m.getAddress() == "/altimeter") {
			altimeter = m.getArgAsFloat(0);
		
		} else if(m.getAddress() == "/battery") {
			battery = m.getArgAsInt32(0);
		
		} else if(m.getAddress() == "/emergency") {
			emergency = m.getArgAsInt32(0);

		} else if(m.getAddress() == "/flying") {
			flying = m.getArgAsInt32(0);

		} else {
			// unrecognized message: display on the bottom of the screen
			string msg_string;
			msg_string = m.getAddress();
			msg_string += ": ";
			for(int i = 0; i < m.getNumArgs(); i++){
				// get the argument type
				msg_string += m.getArgTypeName(i);
				msg_string += ":";
				// display the argument - make sure we get the right type
				if(m.getArgType(i) == OFXOSC_TYPE_INT32){
					msg_string += ofToString(m.getArgAsInt32(i));
				}
				else if(m.getArgType(i) == OFXOSC_TYPE_FLOAT){
					msg_string += ofToString(m.getArgAsFloat(i));
				}
				else if(m.getArgType(i) == OFXOSC_TYPE_STRING){
					msg_string += m.getArgAsString(i);
				}
				else{
					msg_string += "unknown";
				}
			}
			// add to the list of strings to display
			msg_strings[current_msg_string] = msg_string;
			timers[current_msg_string] = ofGetElapsedTimef() + 5.0f;
			current_msg_string = (current_msg_string + 1) % NUM_MSG_STRINGS;
			// clear the next line
			msg_strings[current_msg_string] = "";
		}
		
	}
}

//--------------------------------------------------------------
void testApp::draw(){
	// display instructions
	string buf;
	buf = "sending OSC messages to " + string(HOST) + " " + ofToString(OUTPORT);
	ofDrawBitmapString(buf, 10, 20);
	
	string buf2;
	buf2 = "listening for OSC messages on port " + ofToString(RECVPORT);
	ofDrawBitmapString(buf2, 10, 50);
	
	string gyroscope_s;
	gyroscope_s = "/gyroscope: [" + ofToString(x) + ", " + ofToString(y) + ", " + ofToString(z) + "]";
	ofDrawBitmapString(gyroscope_s, 10, 100);
	
	string velocity_s;
	velocity_s = "/velocity: [" + ofToString(v_x) + ", " + ofToString(v_y) + ", " + ofToString(v_z) + "]";
	ofDrawBitmapString(velocity_s, 10, 120);

	string altimeter_s;
	altimeter_s = "/altimeter: " + ofToString(altimeter);
	ofDrawBitmapString(altimeter_s, 10, 140);

	string battery_s;
	battery_s = "/battery: " + ofToString(battery);
	ofDrawBitmapString(battery_s, 10, 160);

	string emergency_s;;
	emergency_s = "/emergency: " + ofToString(emergency);
	ofDrawBitmapString(emergency_s, 10, 180);

	string flying_s;
	flying_s = "/flying: " + ofToString(flying);
	ofDrawBitmapString(flying_s, 10, 200);
	
	// draw any other incomming messages
	for(int i = 0; i < NUM_MSG_STRINGS; i++){
		ofDrawBitmapString(msg_strings[i], 10, 220 + 15 * i);
	}
}

//--------------------------------------------------------------
void testApp::keyPressed(int key){
	if(key == 'c' || key == 'C'){	
		ofxOscMessage m;
		m.setAddress("/connect");
		sender.sendMessage(m);
	}
	
	if(key == 'd' || key == 'D'){	
		ofxOscMessage m;
		m.setAddress("/disconnect");
		sender.sendMessage(m);
	}
	
	if(key == 'r' || key == 'R'){	
		ofxOscMessage m;
		m.setAddress("/reset");
		sender.sendMessage(m);
	}
	
	if(key == 's' || key == 'S'){	
		ofxOscMessage m;
		m.setAddress("/broadcastSensors");
		m.addIntArg(RECVPORT);
		sender.sendMessage(m);
	}
	
	if(key == 't' || key == 'T'){	
		ofxOscMessage m;
		m.setAddress("/takeOff");
		sender.sendMessage(m);
	}
	
	if(key == 'l' || key == 'L'){	
		ofxOscMessage m;
		m.setAddress("/land");
		sender.sendMessage(m);
	}	
}

//--------------------------------------------------------------
void testApp::keyReleased(int key){
}

//--------------------------------------------------------------
void testApp::mouseMoved(int x, int y){
}

//--------------------------------------------------------------
void testApp::mouseDragged(int x, int y, int button){
}

//--------------------------------------------------------------
void testApp::mousePressed(int x, int y, int button){
}

//--------------------------------------------------------------
void testApp::mouseReleased(int x, int y, int button){
}

//--------------------------------------------------------------
void testApp::windowResized(int w, int h){
}

//--------------------------------------------------------------
void testApp::gotMessage(ofMessage msg){
}

//--------------------------------------------------------------
void testApp::dragEvent(ofDragInfo dragInfo){
}

