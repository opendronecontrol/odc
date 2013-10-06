require 'java'

#### import packages magic #####
module M
  include_package "com.fishuyo.io"
  include_package "com.fishuyo.io.leap"
  include_package "com.fishuyo.maths"
  include_package "com.fishuyo.spatial"
  include_package "com.fishuyo.graphics"
  include_package "com.fishuyo.util"
  include_package "org.opendronecontrol.apps.faceTracking"
end

class Object
  class << self
    alias :const_missing_old :const_missing
    def const_missing c
      M.const_get c
    end
  end
end
###########################

$drone = Main.drone

# $drone.config("maxEulerAngle", 0.25)


########### Keyboard input #############
Keyboard.clear()
Keyboard.use()

fly = false
Keyboard.bind("f", lambda{ 
	if fly
		$drone.land();
	else
		$drone.takeOff();
	end
	fly = !fly 
}) 
Keyboard.bind(" ", lambda{ $drone.land(); puts "land" })


Keyboard.bind("c", lambda{ $drone.connect(); $drone.osc.start(8000) })
Keyboard.bind("x", lambda{ $drone.disconnect(); $drone.osc.stop() })

xzSpeed = 0.7
ySpeed = 1.0
rotSpeed = 0.7
x=0.0
y=0.0
z=0.0
r=0.0
Keyboard.bind("j", lambda{ x=-xzSpeed; $drone.move(x,y,z,r) })
Keyboard.bind("l", lambda{ x=xzSpeed; $drone.move(x,y,z,r) })
Keyboard.bind("i", lambda{ z=-xzSpeed; $drone.move(x,y,z,r) })
Keyboard.bind("k", lambda{ z=xzSpeed; $drone.move(x,y,z,r) })
Keyboard.bind("y", lambda{ y=ySpeed; $drone.move(x,y,z,r) })
Keyboard.bind("h", lambda{ y=-ySpeed; $drone.move(x,y,z,r) })
Keyboard.bind("u", lambda{ r=-rotSpeed; $drone.move(x,y,z,r) })
Keyboard.bind("o", lambda{ r=rotSpeed; $drone.move(x,y,z,r) })
Keyboard.bindUp("j", lambda{ x=0.0 })
Keyboard.bindUp("l", lambda{ x=0.0 })
Keyboard.bindUp("i", lambda{ z=0.0 })
Keyboard.bindUp("k", lambda{ z=0.0 })
Keyboard.bindUp("y", lambda{ y=0.0 })
Keyboard.bindUp("h", lambda{ y=0.0 })
Keyboard.bindUp("u", lambda{ r=0.0 })
Keyboard.bindUp("o", lambda{ r=0.0 })



######## Leap gesture input #########

Leap.clear()
Leap.connect()
Leap.bind( lambda{ |frame|
	return if frame.hands().isEmpty()
	hand = frame.hands().get(0)
	count = hand.fingers().count()
	if count >= 3
		if $drone.hasSensors()
			$drone.takeOff() unless $drone.sensors.get("flying").bool
		end
	elsif count < 2
		$drone.land()
		return
	end

	return unless $drone.sensors.get("flying").bool 

	# return if hand.fingers().count() < 3
	normal = hand.palmNormal()
	dir = hand.direction()
	pos = hand.palmPosition()
	y  = pos.getY() - 160.0

	if y > 0.0 and y < 10.0 then
		y = 0.0
	elsif y > 10.0
		y = y - 10.0
	end

	y = y / 100.0
	y = -1.0 if y < -1.0
	y = 1.0 if y > 1.0

	$drone.move(-normal.roll(), y, dir.pitch() - 0.15, dir.yaw() * 0.0 ) # yaw control disabled because can be confusing!
})

######## Step function called each frame #######

def step(dt)

end





