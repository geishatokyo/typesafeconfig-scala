package com.geishatokyo.typesafeconfig.impl

import com.geishatokyo.typesafeconfig.{ValueType, KeyNotFoundException, Env, TSConfig}
import com.typesafe.config.ConfigException.WrongType
import com.typesafe.config.{ConfigValueType, ConfigException, Config}
import scala.reflect.runtime._
import scala.reflect.runtime.universe._
import scala.collection.JavaConverters._

/**
 * 極力例外を発生させない実装
 * Keyが無い場合は、LaxDefaultsの値が使用される
 * Created by takezoux2 on 2014/06/13.
 */
case class TSConfigWithKey(config : Config,key : String)(implicit protected val env : Env) extends TSConfig with AsSupport{


  override lazy val valueType = {
    if(config.hasPath(key)){
      try{
        if(config.getObject(key).valueType() == ConfigValueType.OBJECT){
          ValueType.Object
        }else{
          ValueType.AnyValue
        }
      }catch{
        case e: WrongType => {
          ValueType.List
        }
      }
    }else{
      ValueType.None
    }
  }

  override def rawConfig = {
    if(valueType == ValueType.Object){
      config.getConfig(this.key)
    }else{
      null
    }
  }

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
      new TSNone(key,env)
    }
  }

  override def exists: Boolean = {
    config.hasPath(key)
  }



  override def asInt: Int = config.getInt(key)

  override def asLong: Long = config.getLong(key)

  override def asString: String = config.getString(key)

  override def asBoolean: Boolean = config.getBoolean(key)

  override def asDouble: Double = config.getDouble(key)


  def asIntList : List[Int] = config.getIntList(key).asScala.toList.map(_.asInstanceOf[Int])
  def asLongList : List[Long] = config.getLongList(key).asScala.toList.map(_.asInstanceOf[Long])
  def asStringList : List[String] = config.getStringList(key).asScala.toList
  def asBooleanList : List[Boolean] = config.getBooleanList(key).asScala.toList.map(_.asInstanceOf[Boolean])
  def asDoubleList : List[Double] = config.getDoubleList(key).asScala.toList.map(_.asInstanceOf[Double])

  override def asList: List[TSConfig] = {
    if(exists){
      try {
        config.getConfigList(key).asScala.toList.map(c => TSConfigRoot(c))
      }catch{
        case e : ConfigException => {
          List(this)
        }
      }
    }else{
      Nil
    }
  }

  override def toString: String = {
    if(exists) {
      try{
        config.getValue(key).toString
      }catch{
        case e : ConfigException.WrongType => {
          config.getConfigList(key).asScala.toString()
        }
      }
    }
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

  override def or(other: TSConfig) = {

    (this.valueType, other.valueType) match{
      case (ValueType.Object, ValueType.Object) => {
        TSConfigRoot(this.rawConfig.withFallback(other.rawConfig))
      }
      case (ValueType.List, ValueType.List) => {
        this
      }
      case (ValueType.List, _ ) => {
        this
      }
      case (ValueType.Object, _) => {
        this
      }
      case (ValueType.AnyValue, _) => {
        this
      }
      case (ValueType.None, _) => {
        other
      }
    }
  }
}

case class TSConfigRoot(config : Config)(implicit protected val env : Env) extends TSConfig with AsSupport{


  override def rawConfig = config

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
      new TSNone(key,env)
    }
  }

  override def exists: Boolean = {
    true
  }




  override def asInt: Int = throw new KeyNotFoundException("<root>")

  override def asLong: Long = throw new KeyNotFoundException("<root>")

  override def asString: String = throw new KeyNotFoundException("<root>")

  override def asBoolean: Boolean = throw new KeyNotFoundException("<root>")

  override def asDouble: Double = throw new KeyNotFoundException("<root>")

  override def asList: List[TSConfig] = {
    List(this)
  }

  def asIntList : List[Int] = Nil
  def asLongList : List[Long] = Nil
  def asStringList : List[String] = Nil
  def asBooleanList : List[Boolean] = Nil
  def asDoubleList : List[Double] = Nil


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

  override def or(other: TSConfig) = {
    if(other.rawConfig != null){
      new TSConfigRoot(this.config.withFallback(other.rawConfig))
    }else{
      this
    }
  }

  override def valueType = ValueType.Object

}