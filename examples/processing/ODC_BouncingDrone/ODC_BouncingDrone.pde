
import java.awt.image.BufferedImage;
import org.opendronecontrol.platforms.ardrone.ARDrone;
import org.opendronecontrol.spatial.Vec3;
import scala.collection.immutable.List;

ARDrone drone;  // this creates our drone class
BufferedImage bimg;  // a 2D image from JAVA returns current video frame
 
PImage img;
PShape ball;
Vec3 gyro; // storing gyroscope data
boolean flying; 
float droneX;
float droneY;
float droneZ;
float droneYaw;
float alti;

float gravity;
float spherePosition;
float vel; // velocity
float bounce;
boolean shouldPhysics;

int radius = 70;

void setup(){
  size(640,480, P3D);

  shouldPhysics = false;
  
  gyro = new Vec3(0.0,0.0,0.0);
  
  ball = createShape(SPHERE,radius);
  
  spherePosition = 0;     
  gravity = 0.4;
  bounce = -0.85;
  
  drone = new ARDrone("192.168.3.1"); // default IP is 192.168.1.1
  drone.connect();
  drone.config("maxVerticalSpeed", 1.0);
  
}

void draw(){
  background(100);

  if (drone.hasSensors()){
    flying = drone.sensors().get("flying").bool();
    gyro = drone.sensors().get("gyroscope").vec();
    alti = drone.sensors().get("altimeter").getFloat();    // height of the drone
   }

   if(flying==true && shouldPhysics == true){
    vel += gravity;
    spherePosition += vel;
      
      if(spherePosition >= height-radius*1.5){     // has the sphere reached the edge of the screen? 
        vel *= bounce;                            // make it bounce
        spherePosition = height-radius*1.5;      // dont go beyond the edge of the screen
    
        droneYaw = random(3)-1.5;                 // randomly spin!

      }
      
     droneY = vel * -0.15;          // scale the velocity to something within range for the drone
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
  rotateY(radians(gyro.y() * -1 ));          // inverting this so when the drone tilts down, so does the sphere
  rotateZ(radians(gyro.z() ));
  shape(ball);
  noFill();  
  popMatrix();
  
  droneY = 0;
  
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


  

