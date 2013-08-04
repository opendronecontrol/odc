package org.opendronecontrol
package numeric

import scala.math.Fractional
import scala.math.Numeric._

import spatial.Vec3
import Vec3.Vec3Numeric
 
trait TimesFloatNumeric[T] extends Numeric[T] {
  def times(x:T, y:Float): T
}
object TimesFloatNumeric {
  trait FloatIsTimesFloatNumeric extends TimesFloatNumeric[Float] {
    def times(x: Float, y:Float) = x*y
  }
  implicit object FloatIsTimesFloatNumeric 
    extends TimesFloatNumeric[Float]
    with FloatIsFractional 
    with Ordering.FloatOrdering
 
  trait DoubleIsTimesFloatNumeric extends TimesFloatNumeric[Double] {
    def times(x: Double,y:Float) = x*y.toDouble
  }
  implicit object DoubleIsTimesFloatNumeric 
    extends DoubleIsTimesFloatNumeric 
    with DoubleIsFractional 
    with Ordering.DoubleOrdering

  trait Vec3IsTimesFloatNumeric extends TimesFloatNumeric[Vec3] {
    def times(x:Vec3,y:Float) = x*y
  }
  implicit object Vec3IsTimesFloatNumeric 
    extends Vec3IsTimesFloatNumeric 
    with Vec3Numeric
}