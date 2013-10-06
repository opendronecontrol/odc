
Drone Simulator

This examples visualizes the SimDrone platform and lets you fly a virtual drone and test out ideas without drone hardware.
The OSC interface is enabled by default on port 8000, so you can control the simulator by sending it commands via OSC.

The program runs and monitors a ruby script sim.rb which is reloaded when saved.  It contains some code to handle keyboard events, macbook multitouch events, and leap events.

Controls

f - toggle flight
i - move forward
j - move left
k - move back
l - move right
y - move up
h - move down

n - add random waypoint to move towards

macbook multitouch events:
  2 finger pan - drone moveTo xz plane
  3 finger pan - drone moveTo xy plane

leap will also control the flight of the drone:
  open hand to takeoff
  close hand to land
  tilt hand to move

