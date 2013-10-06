/*
///    [ODC] Processing Example - BouncingDrone - Sterling Crispin 2013 - sterlingcrispin@gmail.com - http://www.sterlingcrispin.com
///    Press 'u' to takeoff and land
///    Wait until the drone has lifted off, then click and drag to move the sphere and let go to drop the Sphere/drone
///    The drone should 'bounce' with the sphere, try changing the gravity, bounce and velScale constants
///    if you've downloaded this code from the ODC github you may be missing the ODC processing libraries, please visit the ODC website to download them
///    http://www.opendronecontrol.org/
*/
import org.opendronecontrol.platforms.ardrone.ARDrone;
import org.opendronecontrol.spatial.Vec3;

ARDrone drone;  // this creates our drone class
 
PImage img;
PShape ball;
int radius = 70;

Vec3 gyro; // storing gyroscope data
boolean flying; 
float droneX;
float droneY;
float droneZ;
float droneYaw;
float alti;    // height of the drone as reported by the altimeter sensor

float gravity;
float spherePosition;
float vel; // sphere velocity
float velScale; // scaling the sphere velocity to the drone velocity
float bounce;  // reduction of velocity per impact of sphere with 'ground'
boolean shouldPhysics;


void setup(){
  size(640,480, P3D);

  shouldPhysics = false;
  
  gyro = new Vec3(0.0,0.0,0.0);
  
  ball = createShape(SPHERE,radius);
  spherePosition = 0;
  gravity = 0.4;
  bounce = -0.85;
  velScale = -1.5; // be gentle with this variable as changes can result in dramatic behavior, try making it a positive number (1 to 1.5) to make the drone fall up, rather than down
  drone = new ARDrone("192.168.1.1"); // default IP is 192.168.1.1
  drone.connect();
  drone.config("maxVerticalSpeed", 1.0);
  
}

void draw(){
  background(100);

  if (drone.hasSensors()){
    flying = drone.sensors().get("flying").bool();        
    gyro = drone.sensors().get("gyroscope").vec();          //  orientation
    alti = drone.sensors().get("altimeter").getFloat();    // height of the drone
   }

   if(flying==true && shouldPhysics == true){
     vel += gravity;
     spherePosition += vel;
     if(spherePosition >= height-radius*1.5){    // has the sphere reached the edge of the screen?   *1.5 is a fudge factor based on the visual size of the sphere in the draw window
       vel *= bounce;                            // make the sphere bounce
       spherePosition = height-radius*1.5;       // dont go beyond the edge of the screen
       droneYaw = random(3)-1.5;                 // randomly spin upon impact!

      }
      
     droneY = vel * velScale;          // set the vertical move speed of the drone to the sphere velocity and scale the velocity to something within range for the drone
   }
   
   if(flying==true && alti < 0.2){  // fly up if the drone is nearly hitting the ground
     droneY += 1;
   }
     

   if(flying==true && alti > 0.2 && shouldPhysics == true){      // fall and/or spin 
     drone.move(droneX, droneY, droneZ, droneYaw);  
   }
   
   if(flying==true && alti > 0.2 && shouldPhysics == false){            // if the mouse is dragging, 
     drone.tracking().moveTo(0.0, (1.4-(mouseY/height)), 0.0, 0.0);    //  fly to the height of the mouse, relative to the top of the screen
     drone.tracking().step(0.0, alti, 0.0, 0.0);                       //  using the height sensor on the drone
   }
 

  pushMatrix();
  translate( width/2,  spherePosition,  50);
  rotateX(radians(gyro.x() ));                //rotate the sphere with the drones gyroscope data
  rotateY(radians(gyro.y() * -1 ));          // inverting this to match expected behavior, so when the drone tilts down, so does the sphere
  rotateZ(radians(gyro.z() ));
  shape(ball);
  popMatrix();
  
  droneY = 0;     // dont accumulate vertical move speed between frames
  droneYaw *= 0.8; // spin less per frame, so that the drone doesn't spiral constantly
}

void mouseDragged(){
  shouldPhysics = false;
  spherePosition = mouseY;
}

void mouseReleased(){
  shouldPhysics = true;
}

void keyPressed(){
  if (key =='u'){
    if(flying==false){
      drone.takeOff(); 
    } else{
     drone.land(); 
    }
  }
}

void mousePressed(){
  shouldPhysics = false;
  spherePosition = mouseY;
}


  

