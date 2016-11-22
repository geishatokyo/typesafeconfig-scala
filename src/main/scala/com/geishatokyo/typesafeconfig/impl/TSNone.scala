package com.geishatokyo.typesafeconfig.impl

import scala.reflect.runtime.universe._
import com.geishatokyo.typesafeconfig.{ValueType, KeyNotFoundException, Env, TSConfig}
import scala.concurrent.duration.Duration
import java.util.Date
import scala.reflect.runtime.universe._
import com.typesafe.config.Config

/**
 * Created by takezoux2 on 2014/06/13.
 */
case class TSNone(key : String,env : Env) extends TSConfig with AsSupport {


  def /(key : String) : TSConfig = new TSNone(key,env)

  def exists : Boolean = false
  def keys : List[String] = Nil

  override def rawConfig = config
  override def config: Config = null


  def asList : List[TSConfig] = Nil

  override def asInt: Int = throw new KeyNotFoundException(key)

  override def asLong: Long = throw new KeyNotFoundException(key)

  override def asString: String = throw new KeyNotFoundException(key)

  override def asBoolean: Boolean = throw new KeyNotFoundException(key)

  override def asDouble: Double = throw new KeyNotFoundException(key)

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

  override def or(other: TSConfig) = other

  override def valueType = {
    ValueType.None
  }
}
