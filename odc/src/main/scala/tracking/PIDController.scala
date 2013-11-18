
package org.opendronecontrol
package tracking

import numeric._

class PIDController[T](implicit num:TimesFloatNumeric[T]) {

  import num._

  var Kp:T = null.asInstanceOf[T]          // proportional gain on current error
  var Ki:T = null.asInstanceOf[T]          // integral gain/reset on sum of error
  var Kd:T = null.asInstanceOf[T]         // derivative gain/rate

  var dt = 0.f     // change in time in ms
  var t0:Long = 0     // last time in ms

  var setpoint:T = null.asInstanceOf[T]   // destination of process variable
  var last:T = null.asInstanceOf[T]        // last process variable

  var error:T = null.asInstanceOf[T]      // difference setpoint - process variable
  var integral:T = null.asInstanceOf[T]    // accumulated error
  var derivative:T = null.asInstanceOf[T] // derivative of error

  var out:T = null.asInstanceOf[T]        // PID controller output / Manipulated Variable

  def setGains(kp:T,ki:T,kd:T){
    Kp = kp
    Ki = ki
    Kd = kd
  }

  def set(dest:T) = setpoint = dest

  def step(current:T) : Option[T] = {

      if( t0 == 0 ){ 
        t0 = System.currentTimeMillis()
        return None;
      }
      val t = System.currentTimeMillis()
      dt = (t - t0).toFloat
      t0 = t

      val diff = setpoint - current
      derivative = times( (diff - error), (1.f/dt) )
      integral += times( diff, dt )
      error = diff
      
      last = current

      out = Kp*error + Ki*integral + Kd*derivative
      Some(out)
  }

}
