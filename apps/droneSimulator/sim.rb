require 'java'

#### import packages ####
module M
  include_package "com.fishuyo.io"
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

$simDrone = Main.simDrone
$simControl = Main.simControl
# $control = Main.control

$simDrone.sPose.setIdentity() #pos.set(0,0,0)
Main.traces[0].color2.set(0,1,0)

######## Drone Control Config #########

$simControl.posKp.set(0.5,1.5,0.5)
$simControl.posKi.set(0.0,0,0.0)
$simControl.posKd.set(20,40,20)
$simControl.posKdd.set(10,0,10)

# $simControl.setMaxEuler(0.5)
# $control.setMaxEuler(0.4)
# $control.clearEmergency()

########### Keyboard input #############
Keyboard.clear()
Keyboard.use()

fly = false
Keyboard.bind("f", lambda{ 
	if fly
		$simDrone.land();
	else
		$simDrone.takeOff();
	end
	fly = !fly 
}) 

Keyboard.bind("c", lambda{ $control.connect(); })

xzSpeed = 0.7
ySpeed = 1.0
x=0.0
y=0.0
z=0.0
r=0.0
Keyboard.bind("j", lambda{ x=-0.5; $simDrone.move(x,y,z,r) })
Keyboard.bind("l", lambda{ x=xzSpeed; $simDrone.move(x,y,z,r) })
Keyboard.bind("i", lambda{ z=-xzSpeed; $simDrone.move(x,y,z,r) })
Keyboard.bind("k", lambda{ z=xzSpeed; $simDrone.move(x,y,z,r) })
Keyboard.bind("y", lambda{ y=ySpeed; $simDrone.move(x,y,z,r) })
Keyboard.bind("h", lambda{ y=-ySpeed; $simDrone.move(x,y,z,r) })
Keyboard.bindUp("j", lambda{ x=0.0 })
Keyboard.bindUp("l", lambda{ x=0.0 })
Keyboard.bindUp("i", lambda{ z=0.0 })
Keyboard.bindUp("k", lambda{ z=0.0 })
Keyboard.bindUp("y", lambda{ y=0.0 })
Keyboard.bindUp("h", lambda{ y=0.0 })

Keyboard.bind("g", lambda{ Main.togglePlotFollow() })
Keyboard.bind("n", lambda{ 
	ra = Randf.apply(-2.0,2.0,false)
	$simControl.addWaypoint(ra[],1.0,ra[],0)
	$control.addWaypoint(ra[],1.0,ra[],0)
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
			$simControl.moveTo(mx,my,mz,0.0)
	 		# $control.moveTo(mx,my,mz,0.0)
			# $simDrone.move(xx,0,-yy,0)
		end

	# use three fingers to change destination on xy plane
	elsif i == 3
		mx = mx + f[2]*0.05
		my = my + f[3]*0.05
		$simControl.moveTo(mx,my,mz,0.0)
		# $control.moveTo(mx,my,mz,0.0)
		# $simDrone.move(0,yy,0,0)				 
	end

	delay2 = 0 if i != 2
	if mx > 6.0 then mx = 6.0
	elsif mx < -6.0 then mx = -6.0 end
	if my > 6.0 then my = 6.0
	elsif my < 0.0 then my = 0.0 end
	if mz > 6.0 then mz = 6.0
	elsif mz < -6.0 then mz = -6.0 end
})


######## Step function called each frame #######

def step(dt)

	# Step Position Controller
	pos = $simDrone.sPose.pos
	pos = Vec3.new(pos.x,pos.y,pos.z)
	$simControl.step( pos.x,pos.y,pos.z,0.0 )
	
	# add data points to plots
	Main.plots[0].apply($simDrone.sAcceleration.x)
	Main.plots[2].apply($simDrone.sVelocity.x)
	Main.plots[4].apply($simDrone.sPose.pos.x)
	# puts pos

	# add Vec3 point to 3d trace of drones position
	Main.traces[0].apply(pos)

	# have plots follow camera
	if Main.plotsFollowCam
		i=0
		Main.plots.foreach do |p|
			pos = Camera.nav.pos + Camera.nav.uf()*1.5
			j = i/2
			pos += Camera.nav.ur()*(j*0.6-1.0)
			pos += Camera.nav.uu()*0.5

			p.pose.pos.lerpTo( pos, 0.1)
			p.pose.quat.slerpTo( Camera.nav.quat, 0.1)
			i += 1
		end
	end
end

# ### Tracker ###
# TransTrack.clear()
# TransTrack.bind("rigid_body", lambda{|i,f|
# 	if i == 1
# 		yaw = Quat[f[6],f[3],f[4],f[5]].toEuler()._2 * 180.0 / 3.14159
# 		#puts yaw
# 		$control.step(f[0],f[1],f[2],yaw)
# 		#drone.drone.p.quat.set(f[6],f[3],f[4],f[5])
# 		#drone.velocity.set(0,0,0)
# 	end


# 	if i==2
# 		$control.moveTo(f[0],f[1],f[2],0)
# 		#Camera.nav.pos.lerpTo(Vec3.new(f[0],f[1]+1.0,f[2]-1.0),0.05)
# 		#Camera.nav.quat.slerpTo(Quat.new(f[6],f[3],f[4],f[5]),0.05)
# 		#Camera.nav.quat.set(f[6],f[3],f[4],f[5])
# 	end

# })
# TransTrack.setDebug(false)
# TransTrack.start()




