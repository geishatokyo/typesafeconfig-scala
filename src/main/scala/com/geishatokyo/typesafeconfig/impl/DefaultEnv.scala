package com.geishatokyo.typesafeconfig.impl

import com.geishatokyo.typesafeconfig.{NoValueException, ValueGetter, TSConfig, Env}
import scala.reflect.runtime.universe._
import java.util.Date
import scala.concurrent.duration.Duration
import com.typesafe.config.{ConfigValueFactory, ConfigValue, Config}
import scala.collection.JavaConverters._
import java.text.SimpleDateFormat

/**
 *
 * Defaul values and default deserializing.
 *
 *
 */
class DefaultEnv extends Env{



  def as(c : ValueGetter)(implicit mirror: Mirror) : PartialFunction[Type,Any] = {
    case t if t =:= typeOf[Int] => c.asInt
    case t if t =:= typeOf[Long] => c.asLong
    case t if t =:= typeOf[Double] => c.asDouble
    case t if t =:= typeOf[Boolean] => c.asBoolean
    case t if t =:= typeOf[String] => c.asString
    case t if t =:= typeOf[List[Int]] => c.asIntList
    case t if t =:= typeOf[List[Long]] => c.asLongList
    case t if t =:= typeOf[List[Double]] => c.asDoubleList
    case t if t =:= typeOf[List[Boolean]] => c.asBooleanList
    case t if t =:= typeOf[List[String]] => c.asStringList
    case t => {
      genericMatch(c,t)
    }
  }


  private def genericMatch(config : ValueGetter,t : Type)(implicit mirror: Mirror ) = t match {
    case t if t <:< typeOf[Map[String,_]] => {
      mapToMap(config,t.typeArgs(1))
    }
    case t if t <:< typeOf[List[_]] => config.asList.map(c => as(c).apply(t.typeArgs(0))).toList
    case t if t <:< typeOf[Set[_]] => config.asList.map(as(_).apply(t.typeArgs(0))).toSet
    case t if t <:< typeOf[Option[_]] => Some(as(config).apply(t.typeArgs(0)))
    case t if t =:= typeOf[Duration] => {
      Duration(config.asString)
    }
    case t if t <:< typeOf[Seq[_]] => config.asList.map(as(_).apply(t.typeArgs(0))).toSeq
    case t => {
      mapObject(config,t)
    }
  }

  private def mapToMap(config : ValueGetter,valueType : Type)(implicit mirror : Mirror) : Map[String,_] = {

    config.keys.map(k => {
      val e = new ValueOverwriter(config / k,
        Map("name" -> k,"id" -> k,"key" -> k)
      )
      k -> as(e).apply(valueType)
    }).toMap
  }

  class ValueOverwriter(v : ValueGetter,values : Map[String,String]) extends ValueGetter{
    override type Self = ValueGetter

    override def asLong: Long = v.asLong

    override def /(key: String): Self = {
      val c = v / key
      if(c.exists) c
      else {
        values.get(key) match{
          case Some(v) => new StringValueGetter(c,v)
          case None => c
        }
      }
    }

    override def asString: String = v.asString
    override def asBoolean: Boolean = v.asBoolean
    override def asInt: Int = v.asInt
    override def asDouble: Double = v.asDouble

    def asIntList : List[Int] = v.asIntList
    def asLongList : List[Long] = v.asLongList
    def asStringList : List[String] = v.asStringList
    def asBooleanList : List[Boolean] = v.asBooleanList
    def asDoubleList : List[Double] = v.asDoubleList
    override def keys: List[String] = v.keys
    override def exists: Boolean = v.exists

    override def asList: List[ValueGetter] = v.asList
  }


  class StringValueGetter(forNone : ValueGetter,str : String) extends ValueGetter{
    override type Self = ValueGetter


    override def asLong: Long = try{str.toLong}catch{
      case e : Throwable => forNone.asLong
    }

    override def /(key: String): Self = forNone

    override def asString: String = str

    override def asBoolean: Boolean = try{str.toBoolean}catch{
      case e : Throwable => forNone.asBoolean
    }

    override def asInt: Int = try { str.toInt} catch{
      case e : Throwable => forNone.asInt
    }

    override def asDouble: Double = try{str.toDouble} catch{
      case e : Throwable => forNone.asDouble
    }

    def asIntList : List[Int] = Nil
    def asLongList : List[Long] = Nil
    def asStringList : List[String] = Nil
    def asBooleanList : List[Boolean] = Nil
    def asDoubleList : List[Double] = Nil

    override def keys: List[String] = Nil

    override def exists: Boolean = true

    override def asList: List[ValueGetter] = List(this)
  }



  private def mapObject(config : ValueGetter,tpe : Type)(implicit mirror : Mirror) : Any = DefaultEnv.synchronized{

    // map classes
    val constructor = tpe.members.collectFirst({
      case m : MethodSymbol if m.isPrimaryConstructor => m
    }).getOrElse({
      throw new Exception("Can't find primary constructor in " + tpe)
    })

    lazy val companionInstanceMirror = {
      val module = mirror.reflectModule(tpe.typeSymbol.companion.asModule).instance
      mirror.reflect(module)
    }

    def getDefaultValue(index : Int) = {

      val dfltMName = {
        import scala.reflect.runtime.universe
        import scala.reflect.internal._
        val ds = universe.asInstanceOf[Definitions with SymbolTable with StdNames]
        ds.nme.defaultGetterName(ds.newTermName("apply"),index + 1)
      }
      tpe.companion.decl(TermName(dfltMName.encoded)) match{
        case m : MethodSymbol => {
          Some(companionInstanceMirror.reflectMethod(m).apply())
        }
        case _ => None
      }
    }

    val constructorParams = constructor.paramss.flatten.zipWithIndex.map({
      case (p,index) => {
        val name = p.name.encodedName.toString
        val v = config / name

        val pf = as(v)

        def defaultValue = {
          getDefaultValue(index) match{
            case Some(v) => v
            case None => {
              if(p.typeSignature <:< typeOf[Option[_]]) None
              else throw new NoValueException(tpe.toString,name)
            }
          }
        }

        if(v.exists && pf.isDefinedAt(p.typeSignature)) {
          try{
            as(v).apply(p.typeSignature)
          }catch{
            case e : Throwable => defaultValue
          }
        }
        else {
          defaultValue
        }
      }
    })
    val classMirror = mirror.reflectClass(tpe.typeSymbol.asClass)
    val instance = classMirror.reflectConstructor(constructor)(constructorParams :_*)


    instance
  }
}

object DefaultEnv extends DefaultEnv