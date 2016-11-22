package com.geishatokyo.typesafeconfig

/**
  * Created by takezoux2 on 2016/11/22.
  */
sealed trait ValueType {

}


object ValueType{
  case object Object extends ValueType
  case object List extends ValueType
  case object AnyValue extends ValueType
  case object None extends ValueType
}