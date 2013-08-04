
package org.opendronecontrol
package spatial

object Vec3 {

  def apply() = new Vec3(0,0,0)
  def apply(v:Vec3) = new Vec3(v.x,v.y,v.z)
  def apply( v: Float=0.f) = new Vec3( v, v, v)
  def apply( vv: Double) = { val v=vv.toFloat; new Vec3( v, v, v) }
  def apply( x: Float, y: Float, z: Float) = new Vec3(x,y,z)
  def apply( x: Double, y: Double, z: Double) =  new Vec3(x.toFloat,y.toFloat,z.toFloat) 

  def unapply( v: Vec3): Some[(Float,Float,Float)] = Some((v.x,v.y,v.z))

  trait Vec3Numeric extends math.Numeric[Vec3] {
    def plus(x: Vec3, y: Vec3) = x+y
    def minus(x: Vec3, y: Vec3) = x-y
    def times(x: Vec3, y: Vec3) = x*y
    def negate(x: Vec3): Vec3 = -x
    def fromInt(x: Int) = Vec3(x)
    def toInt(x: Vec3) = x.mag().toInt
    def toLong(x: Vec3) = x.mag().toLong
    def toFloat(x: Vec3) = x.mag()
    def toDouble(x: Vec3) = x.mag().toDouble
    def compare(x:Vec3,y:Vec3) = (x.mag() - y.mag()).toInt
  }
  implicit object Vec3Numeric extends Vec3Numeric
}

class Vec3( var x: Float, var y: Float, var z: Float ){

  def apply(i:Int) = i match { case 0 => x; case 1 => y; case 2 => z;}
  def update(i:Int,v:Float) = i match { case 0 => x=v; case 1 => y=v; case 2 => z=v;}

  def set(v:Vec3) = { x=v.x; y=v.y; z=v.z }
  def set(v:Float) = { x=v; y=v; z=v }
  def set(a:Float,b:Float,c:Float) = { x=a; y=b; z=c; }
  def set(v:(Float,Float,Float)) = { x=v._1; y=v._2; z=v._3 }

  def +(v: Vec3) = Vec3( x+v.x, y+v.y, z+v.z )
  def +=(v: Vec3) = { x+=v.x; y+=v.y; z+=v.z }
  def -(v: Vec3) = Vec3( x-v.x, y-v.y, z-v.z )
  def -=(v: Vec3) = { x-=v.x; y-=v.y; z-=v.z }
  def unary_- = Vec3( -x, -y, -z ) 
  def *(s: Float ) = Vec3(s*x, s*y, s*z)
  def *(v: Vec3 ) = Vec3(v.x*x, v.y*y, v.z*z)
  def *=(s: Float) = { x*=s; y*=s; z*=s }
  def /(s: Float ) = Vec3(x/s, y/s, z/s)
  
  def dot(v: Vec3) : Float = x*v.x + y*v.y + z*v.z
  def cross( v: Vec3) = Vec3( y*v.z - z*v.y, z*v.x - x*v.z, x*v.y - y*v.x )
  def magSq() = this dot this
  def mag() = math.sqrt( magSq() ).toFloat
  def normalize() = this * (1.0f / mag() )

  def zero() = {x=0;y=0;z=0}
  override def toString() = "[" + x + " " + y + " " + z + "]"

  def lerp( v:Vec3, d:Float ) = this + (v-this)*d
  def lerpTo( v:Vec3, d:Float) = this.set(this.lerp(v,d))
  
}




