package com.geishatokyo.typesafeconfig.impl

import scala.reflect.runtime.universe._
import com.geishatokyo.typesafeconfig.{Env, TSConfig}
import scala.concurrent.duration.Duration
import java.util.Date
import scala.reflect.runtime.universe._

/**
 * Created by takezoux2 on 2014/06/13.
 */
class TSNone(env : Env) extends TSConfig {


  def /(key : String) : TSConfig = this

  def exists : Boolean = false
  def keys : List[String] = Nil


  def as(tpe : Type)(implicit mirror : Mirror) : Any = {
    env.defaults.applyOrElse(tpe,(t : Type) => null)
  }
  def asList : List[TSConfig] = Nil

  override def asInt: Int = env.getDefault[Int]

  override def asLong: Long = env.getDefault[Long]

  override def asString: String = env.getDefault[String]

  override def asBoolean: Boolean = env.getDefault[Boolean]

  override def asDouble: Double = env.getDefault[Double]

  def asIntList : List[Int] = Nil
  def asLongList : List[Long] = Nil
  def asStringList : List[String] = Nil
  def asBooleanList : List[Boolean] = Nil
  def asDoubleList : List[Double] = Nil

  override def hashCode(): Int = 0

  override def toString: String = {
    "<none>"
  }

  override def equals(obj: scala.Any): Boolean = {
    obj match{
      case tsConf : TSConfig => {
        tsConf.isEmpty
      }
      case _ => false
    }
  }
}
