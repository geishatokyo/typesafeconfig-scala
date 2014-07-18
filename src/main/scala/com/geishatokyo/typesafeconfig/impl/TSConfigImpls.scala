package com.geishatokyo.typesafeconfig.impl

import com.geishatokyo.typesafeconfig.{Env, TSConfig}
import com.typesafe.config.{ConfigException, Config}
import scala.reflect.runtime._
import scala.reflect.runtime.universe._
import scala.collection.JavaConverters._

/**
 * 極力例外を発生させない実装
 * Keyが無い場合は、LaxDefaultsの値が使用される
 * Created by takezoux2 on 2014/06/13.
 */
case class TSConfigWithKey(config : Config,key : String)(implicit protected val env : Env) extends TSConfig with AsSupport{

  def keys = {
    if(exists) {
      config.getConfig(key).entrySet().asScala.map(es => {
        val k = es.getKey
        val i = k.indexOf(".")
        if(i > 0){
          k.substring(0,i)
        }else{
          k
        }
      }).toList.distinct
    } else Nil
  }

  override def /(key: String): TSConfig = {
    if(exists){
      TSConfigWithKey(config.getConfig(this.key),key)
    }else{
      env.none
    }
  }

  override def exists: Boolean = {
    config.hasPath(key)
  }

  override def asList: List[TSConfig] = {
    if(exists){
      try {
        config.getConfigList(key).asScala.toList.map(c => TSConfigRoot(c))
      }catch{
        case e : ConfigException => {
          Nil
        }
      }
    }else{
      Nil
    }
  }

  override def toString: String = {
    if(exists) config.atPath(key).toString
    else "<none>"
  }

  override def hashCode(): Int = {
    if(exists) config.atPath(key).hashCode()
    else 0
  }

  override def equals(obj: scala.Any): Boolean = {
    obj match{
      case conf : TSConfigWithKey => {
        if(conf.exists && this.exists){
          conf.config.atPath(conf.key).equals(this.config.atPath(this.key))
        }else false
      }
      case conf : TSConfigRoot => {
        conf.equals(this)
      }
      case _ => {
        false
      }
    }
  }
}

case class TSConfigRoot(config : Config)(implicit protected val env : Env) extends TSConfig with AsSupport{



  override def key: String = ""

  def keys = {
    if(exists) {
      config.entrySet().asScala.map(es => {
        val k = es.getKey
        val i = k.indexOf(".")
        if(i > 0){
          k.substring(0,i)
        }else{
          k
        }
      }).toList.distinct
    } else Nil
  }

  override def /(key: String): TSConfig = {
    if(exists){
      TSConfigWithKey(config,key)
    }else{
      env.none
    }
  }

  override def exists: Boolean = {
    true
  }


  override def asList: List[TSConfig] = {
    Nil
  }


  override def toString: String = {
    config.toString
  }


  override def hashCode(): Int = {
    config.hashCode()
  }

  override def equals(obj: scala.Any): Boolean = {
    obj match{
      case conf : TSConfigRoot => {
        this.config.equals(conf.config)
      }
      case conf : TSConfigWithKey => {
        if(conf.exists){
          conf.config.atPath(conf.key).equals(this.config)
        }else false
      }
      case _ => false
    }
  }
}