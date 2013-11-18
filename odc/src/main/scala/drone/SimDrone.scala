
package org.opendronecontrol
package drone

import spatial._

/*  Very rudimentary simulation of a quadcoptor */

class SimDrone extends DroneBase {

  // simulation state -- represents the position velocity and acceleration for a given time
  var sPose = Pose()
  var sVelocity = Vec3()
  var sAcceleration = Vec3()
  var thrust = 0.f

  var g = Vec3(0,-9.8f,0)
  var mass = .433f // kg
  var kair = .5f

  var flying = false
  var takingOff = false

  var controls = Vec3()
  var rot = 0.f

  var maxEuler = .3f  
  var maxVert = 1.f   
  var maxRot = 3.0f  

  var timeout = 0.f  

  def connect(){}
  def disconnect(){}

  def takeOff() = {
    flying = true
    takingOff = true
  }
  def land() = { sPose.quat.setIdentity(); flying = false; takingOff=false; thrust = 5.f}

  def move(x:Float,y:Float,z:Float,r:Float){
    if(!flying) return
    controls.set(x,y,z)
    rot = r
    sPose.quat.fromEuler(z*maxEuler,0.f,-x*maxEuler)
    thrust = (-g.y + y)/sPose.uu().y
    timeout = 0.f
  }

  def move(q:Quat){
    sPose.quat.set(q)
    thrust = (-g.y)/sPose.uu().y
  }

  override def hover() = { sPose.quat.setIdentity(); sVelocity.set(0,0,0)}


  def step(dt:Float){

    val p = sPose.pos 
    val q = sPose.quat

    val angles = q.toEuler()

    if( takingOff && p.y < 0.25f){
      thrust = 9.8f + 2.f
    } else if( takingOff ){
      thrust = 9.8f
      takingOff = false
    }

    sAcceleration.set( sPose.uu()*thrust )

    sPose.pos += sVelocity*dt
    sVelocity += (sAcceleration + g)*dt - sVelocity*kair*dt 

    if( p.y < 0.f){
      sPose.pos.y = 0.f
      if( flying ) sVelocity = sVelocity * Vec3( .5f, -.5f, .5f)
      else sVelocity.zero()
    }

    timeout += dt
    if( timeout > .42f) move(0,0,0,0)

  }



  
}