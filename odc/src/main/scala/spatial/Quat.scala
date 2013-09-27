/* 
* Quaternions!
*   Port of al_Quat.hpp from AlloSystem
*   https://github.com/AlloSphere-Research-Group/AlloSystem/blob/master/allocore/allocore/math/al_Quat.hpp 
*/

package org.opendronecontrol
package spatial

object Quat {
  val eps = 0.0000001
  val acc_max = 1.000001
  val acc_min = 0.999999
  def apply( w:Float, x:Float, y:Float, z:Float ) = new Quat(w,x,y,z)
  def apply(q:Quat) = new Quat(q.w,q.x,q.y,q.z)
  def apply() = new Quat(1,0,0,0)
}

class Quat(var w:Float, var x:Float, var y:Float, var z:Float ){
  implicit def toF( d: Double ) = d.toFloat

  def unary_- = Quat( -w, -x, -y, -z ) 
  def +(v: Quat) = Quat( w+v.w, x+v.x, y+v.y, z+v.z )
  def -(v: Quat) = Quat( w-v.w, x-v.x, y-v.y, z-v.z )
  def *(q: Quat) = Quat( w*q.w-x*q.x-y*q.y-z*q.z, w*q.x+x*q.w+y*q.z-z*q.y, w*q.y+y*q.w+z*q.x-x*q.z, w*q.z+z*q.w+x*q.y-y*q.x )
  def *(s: Float ) = Quat(s*w, s*x, s*y, s*z)
  def /(s: Float ) = Quat(w/s, x/s, y/s, z/s)
  
  def +=(v: Quat) = { w+=v.w; x+=v.x; y+=v.y; z+=v.z }
  def -=(v: Quat) = { w-=v.w; x-=v.x; y-=v.y; z-=v.z }
  def *=(q: Quat) = set(this*q)
  def *=(s: Float) = { w*=s; x*=s; y*=s; z*=s }
  
  def set(q: Quat) = { w=q.w; x=q.x; y=q.y; z=q.z }
  def set(qw:Float,a:Float,b:Float,c:Float) = { x=a; y=b; z=c; w=qw }

  def dot(v: Quat) : Float = w*v.w + x*v.x + y*v.y + z*v.z
  //def cross( v: Quat) = Quat( y*v.z - z*v.y, z*v.x - x*v.z, x*v.y - y*v.x )
  def magSq() = this dot this
  def mag() = math.sqrt( magSq() )
  def normalize() = {
    val m = magSq()
    if( m*m < Quat.eps ) Quat().setIdentity()
    else if( m > Quat.acc_max || m < Quat.acc_min ) this * (1.0f / math.sqrt( m ))
    else this
  }

  def conj = Quat( w, -x,-y,-z )
  def sgn = Quat(w,x,y,z).normalize
  def inverse = sgn conj
  def recip = conj / magSq   

  def zero() = {w=0;x=0;y=0;z=0;this}
  def setIdentity() = {w=1;x=0;y=0;z=0;this}

  def fromAxisX( ang: Float ) = {w=math.cos(ang*.5f);x=math.sin(ang*.5f);y=0;z=0}
  def fromAxisY( ang: Float ) = {w=math.cos(ang*.5f);x=0;y=math.sin(ang*.5f);z=0}
  def fromAxisZ( ang: Float ) = {w=math.cos(ang*.5f);x=0;y=0;z=math.sin(ang*.5f)}
  def fromAxisAngle( ang: Float, axis:Vec3 ) = { 
    val sin2a = math.sin(ang*.5f)
    w = math.cos(ang*.5f)
    x = axis.x * sin2a
    y = axis.y * sin2a
    z = axis.z * sin2a
  }
  // from euler angles ( elevation, azimuth, bank )
  def fromEuler( eu:Vec3 ) : Quat = fromEuler((eu.x,eu.y,eu.z))
  def fromEuler( eu:(Float,Float,Float) ) : Quat= { //eu = Vec3( el, az, ba )
    val c1 = math.cos(eu._1*.5f); val c2 = math.cos(eu._2*.5f); val c3 = math.cos(eu._3*.5f)   
    val s1 = math.sin(eu._1*.5f); val s2 = math.sin(eu._2*.5f); val s3 = math.sin(eu._3*.5f) 
    val tw = c2*c1; val tx = c2*s1; val ty = s2*c1; val tz = -s2*s1
    w = tw*c3 - tz*s3; x = tx*c3 + ty*s3; y = ty*c3 - tx*s3; z = tw*s3 + tz*c3
    this
  }

  //local unit vectors
  def toX() = Vec3(1.0 - 2.0*y*y - 2.0*z*z, 2.0*x*y + 2.0*z*w, 2.0*x*z - 2.0*y*w)
  def toY() = Vec3(2.0*x*y - 2.0*z*w, 1.0 - 2.0*x*x - 2.0*z*z, 2.0*y*z + 2.0*x*w)
  def toZ() = Vec3(2.0*x*z + 2.0*y*w, 2.0*y*z - 2.0*x*w, 1.0 - 2.0*x*x - 2.0*y*y)

  def toEuler() : (Float,Float,Float) = {
    val az = math.asin( -2.0 * (x*z - w*y))
    val el = math.atan2( 2.0 * (y*z + w*x), w*w - x*x - y*y + z*z)
    val bank = math.atan2( 2.0 * (x*y + w*z), w*w + x*x - y*y - z*z)
    (el,az,bank)
  }
  def toEulerVec() : Vec3 = {
    val az = math.asin( -2.0 * (x*z - w*y))
    val el = math.atan2( 2.0 * (y*z + w*x), w*w - x*x - y*y + z*z)
    val bank = math.atan2( 2.0 * (x*y + w*z), w*w + x*x - y*y - z*z)
    Vec3(el,az,bank)
  }

  def slerp(q:Quat, d:Float): Quat = {
    var (a,b) = (0.f,0.f)
    var negb = false
    var dotprod = dot(q)

    if( dotprod < -1 ) dotprod = -1
    else if( dotprod > 1 ) dotprod = 1

    if( dotprod < 0){
      dotprod = -dotprod
      negb = true
    }

    val ang = math.acos( dotprod )
    if( math.abs(ang) > Quat.eps ){
      val sini = 1.f / math.sin(ang)
      a = math.sin(ang * (1.-d))*sini
      b = math.sin(ang*d)*sini
      if(negb) b = -b
    } else {
      a = d
      b = 1.-d
    }

    val quat = Quat(a*w+b*q.w, a*x+b*q.x, a*y+b*q.y, a*z+b*q.z)
    quat.normalize
  }
  def slerpTo(q:Quat, d:Float) = this.set( this.slerp(q,d))
  
  def rotate(v:Vec3) = {
    val p = Quat(
      -x*v.x - y*v.y - z*v.z,
       w*v.x + y*v.z - z*v.y,
       w*v.y - x*v.z + z*v.x,
       w*v.z + x*v.y - y*v.x
    )
    Vec3(
      p.x*w - p.w*x + p.z*y - p.y*z,
      p.y*w - p.w*y + p.x*z - p.z*x,
      p.z*w - p.w*z + p.y*x - p.x*y
    )
  }

  def getRotationTo(src:Vec3, dst:Vec3):Quat = {
    val q = Quat()
    
    val d = src dot dst
    if (d >= 1.f) {
      // vectors are the same
      return q;
    }
    if (d < -0.999999999f) {
      // pick an axis to rotate around
      var axis = Vec3(0, 1, 0) cross src
      // if colinear, pick another:
      if (axis.magSq() < 0.00000000001f) {
        axis = Vec3(0, 0, 1) cross src
      }
      //axis.normalize();
      q.fromAxisAngle(math.Pi, axis);
    } else {
      val s = math.sqrt((d+1.f)*2.f)
      val invs = 1./s
      val c = src cross dst
      q.x = c(0) * invs;
      q.y = c(1) * invs;
      q.z = c(2) * invs;
      q.w = s * 0.5;
    }
    return q.normalize();
  }

  override def toString() = "[" + w + " " + x + " " + y + " " + z + "]"
}


