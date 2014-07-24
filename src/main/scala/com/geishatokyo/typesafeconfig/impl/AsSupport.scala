package com.geishatokyo.typesafeconfig.impl

import com.geishatokyo.typesafeconfig.{UnsupportedTypeException, KeyNotFoundException, Env, TSConfig}
import scala.reflect.runtime.universe._
import com.typesafe.config.{ConfigException, Config}
import scala.collection.JavaConverters._
import scala.concurrent.duration.Duration
import java.util.Date

/**
 * Created by takezoux2 on 2014/06/13.
 */
trait AsSupport { self : TSConfig =>

  def config : Config
  def key : String

  protected def env : Env

  protected def optionTest(tpe : Type) = {
    tpe match {
      case t if t <:< typeOf[Option[_]] => Some(None)
      case t if t <:< typeOf[List[_]] => Some(Nil)
      case t if t <:< typeOf[Set[_]] => Some(Set())
      case t if t <:< typeOf[Map[_, _]] => Some(Map())
      case t if t <:< typeOf[Seq[_]] => Some(Seq())
      case _ => None
    }
  }


  def as(tpe: Type)(implicit mirror: Mirror): Any = ReflectionLock.synchronized{


    if(tpe =:= typeOf[TSConfig]) return this

    if(!exists) {
      optionTest(tpe).getOrElse({
        throw new KeyNotFoundException(key)
      })
    }else{
      env.as(this).applyOrElse(tpe, (a: Type) => {
        throw new UnsupportedTypeException(a.toString)
      })
    }
  }
}

object ReflectionLock
