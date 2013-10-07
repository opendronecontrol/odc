#pragma once

#include "ofMain.h"
#include "ofxOsc.h"

#define HOST "localhost"
#define OUTPORT 8000
#define RECVPORT 8001
#define NUM_MSG_STRINGS 4

//--------------------------------------------------------
class testApp : public ofBaseApp {

	public:

		void setup();
		void update();
		void draw();

		void keyPressed(int key);
		void keyReleased(int key);
		void mouseMoved(int x, int y);
		void mouseDragged(int x, int y, int button);
		void mousePressed(int x, int y, int button);
		void mouseReleased(int x, int y, int button);
		void windowResized(int w, int h);
		void dragEvent(ofDragInfo dragInfo);
		void gotMessage(ofMessage msg);

		ofTrueTypeFont font;
		ofxOscSender sender;
		ofxOscReceiver receiver;
	
		int current_msg_string;
		string msg_strings[NUM_MSG_STRINGS];
		float timers[NUM_MSG_STRINGS];
	
		float x, y, z; // gyroscope
		float v_x, v_y, v_z; // velocity
		int flying, battery, emergency;
		float altimeter;
	
};

