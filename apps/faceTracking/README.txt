
Face Tracking

This example lets you control the drone with a Leap Motion Controller. It also displays the drone's video stream with tracked faces in green squares. Doesn't use the face data for much yet..

The program runs and monitors a ruby script face.rb which is reloaded when saved.  It contains some code to handle keyboard events, and leap events.

Controls

c - connect to drone
x - disconnect from drone

f - toggle flight
i - move forward
j - move left
k - move back
l - move right
y - move up
h - move down
u - rotate left
o - rotate right


leap will also control the flight of the drone:
  open hand to takeoff
  close hand to land
  tilt hand to move

