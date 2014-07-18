package com.geishatokyo.typesafeconfig

/**
 * Created by takezoux2 on 2014/07/18.
 */
trait ValueGetter {

  type Self <: ValueGetter

  def exists : Boolean

  def asInt : Int
  def asLong : Long
  def asString : String
  def asBoolean : Boolean
  def asDouble : Double
  def asIntList : List[Int]
  def asLongList : List[Long]
  def asStringList : List[String]
  def asBooleanList : List[Boolean]
  def asDoubleList : List[Double]

  def asList : List[ValueGetter]

  def /(key : String) : Self
  def keys : List[String]

}
