require 'java'

#### import packages ####
module M
  include_package "com.fishuyo.io"
  include_package "com.fishuyo.io.leap"
  include_package "com.fishuyo.maths"
  include_package "com.fishuyo.spatial"
  include_package "com.fishuyo.graphics"
  include_package "com.fishuyo.util"
  include_package "org.opendronecontrol.apps.sim"
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

$drone = Main.simDrone

Main.simDrone.sPose.setIdentity()
Main.trace.color1.set(1,1,1)

######## Drone Control Config #########

# $drone.tracking.posKp.set(0.5,1.5,0.5)
# $drone.tracking.posKi.set(0.0,0,0.0)
# $drone.tracking.posKd.set(20,40,20)
# $drone.tracking.posKdd.set(0,0,0)


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

Keyboard.bind("n", lambda{ 
	ra = Randf.apply(-2.0,2.0,false)
	$drone.tracking.addWaypoint(ra[],1.0,ra[],0)
})

Keyboard.bind("t", lambda{ 
	$drone.tracking.stop()
})


######## Trackpad gesture input #########

mx=0.0
my=0.0
mz=0.0
delay2 = 0
Trackpad.clear()
Trackpad.connect()
Trackpad.bind( lambda{|i,f|      # i -> number of fingers detected
							     # f -> array of (x,y,dx,dy)
	xx = f[0]*2 - 1
	yy = f[1]*2 - 1	

	# use two fingers to change destination on xz plane
	if i == 2
		delay2 += 1
		if delay2 > 5				 
			mx = mx + f[2]*0.05
			mz = mz + f[3]*-0.05
			$drone.tracking.moveTo(mx,my,mz,0.0)
		end

	# use three fingers to change destination on xy plane
	elsif i == 3
		mx = mx + f[2]*0.05
		my = my + f[3]*0.05
		$drone.tracking.moveTo(mx,my,mz,0.0)
	end

	delay2 = 0 if i != 2

	if mx > 6.0 then mx = 6.0
	elsif mx < -6.0 then mx = -6.0 end
	if my > 6.0 then my = 6.0
	elsif my < 0.0 then my = 0.0 end
	if mz > 6.0 then mz = 6.0
	elsif mz < -6.0 then mz = -6.0 end
})



######## Leap gesture input #########

Leap.clear()
Leap.connect()
Leap.bind( lambda{ |frame|
	return if frame.hands().isEmpty()
	hand = frame.hands().get(0)
	count = hand.fingers().count()
	if count >= 3
		$drone.takeOff() 
	elsif count < 2
		$drone.land()
		return
	end

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

	$drone.move(-normal.roll(),y,dir.pitch() - 0.15 , dir.yaw() * 0.0 )
})

######## Step function called each frame #######

def step(dt)

	# Step Position Controller
	pos = Main.simDrone.sPose.pos
	Main.simDrone.tracking.step( pos.x,pos.y,pos.z,0.0 )

	# update moveTo cube
	dest = $drone.tracking.destPose.pos
	Main.moveCube.pose.pos.set(dest.x,dest.y,dest.z)

	# add Vec3 point to 3d trace of drones position
	pos = Vec3.new(pos.x,pos.y,pos.z)
	Main.trace.apply(pos)

	Shader.lightPosition.set( pos + Vec3.new(0,5,0))

end




