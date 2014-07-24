package com.geishatokyo.typesafeconfig

/**
 * Created by takezoux2 on 2014/07/24.
 */

class TSConfigException(m : String) extends Exception(m)


case class KeyNotFoundException(key : String) extends TSConfigException(s"Key:${key} not found")

case class NoValueException(className : String,paramName : String)
  extends TSConfigException( s"Param:${paramName} in ${className} constructor has no value!Add key or define default value." ) {

}

case class UnsupportedTypeException(className : String) extends TSConfigException(s"Can't convert to ${className}")

