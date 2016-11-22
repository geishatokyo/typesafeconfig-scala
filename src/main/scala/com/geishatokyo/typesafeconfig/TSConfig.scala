package com.geishatokyo.typesafeconfig

import com.typesafe.config.Config

import scala.reflect._
import scala.reflect.runtime._
import scala.reflect.runtime.universe._
import java.util.Date
import scala.concurrent.duration.Duration

/**
 * Created by takezoux2 on 2014/06/13.
 */
trait TSConfig extends ValueGetter {

  type Self = TSConfig

  def rawConfig: Config

  def ? = exists
  def isEmpty = !exists

  def as(t : Type)(implicit mirror : Mirror) : Any

  def as[T : TypeTag] : T = {
    implicit val mirror = getMirror(implicitly[TypeTag[T]])
    as(typeOf[T]).asInstanceOf[T]
  }

  def get[T : TypeTag] : Option[T] = {
    if(exists) {
      implicit val mirror = getMirror(implicitly[TypeTag[T]])
      Some(as(typeOf[T]).asInstanceOf[T])
    }else None
  }

  def asList[T : TypeTag] : List[T] = {
    implicit val mirror = getMirror(implicitly[TypeTag[T]])
    as(typeOf[List[T]])(mirror).asInstanceOf[List[T]]
  }
  def asList : List[TSConfig]

  def asMapOf[T : TypeTag] : Map[String,T] = {
    implicit val mirror = getMirror(implicitly[TypeTag[T]])
    as(typeOf[Map[String,T]])(mirror).asInstanceOf[Map[String,T]]
  }

  def asDate = as[Date]
  def duration = as[Duration]

  protected def getMirror(t : TypeTag[_]) = {
    if(Thread.currentThread().getContextClassLoader != null) {
      // First try to use context class loader.
      runtimeMirror(Thread.currentThread().getContextClassLoader)
    }else{
      t.mirror
    }
  }

  def or(other: TSConfig) : TSConfig


  def valueType : ValueType

}




