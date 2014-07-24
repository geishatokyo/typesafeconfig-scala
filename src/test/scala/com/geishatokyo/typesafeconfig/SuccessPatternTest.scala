package com.geishatokyo.typesafeconfig

import org.scalatest.{Matchers, FlatSpec}
import com.geishatokyo.typesafeconfig.impl.DefaultEnv
import com.typesafe.config.ConfigException

/**
 * Lax=緩い値解決をするlax系クラスのテスト
 * Created by takezoux2 on 2014/06/13.
 */
class SuccessPatternTest extends FlatSpec with Matchers {


  "Not exist path" should "return exists == false" in {
    val conf = TSConfigFactory.parseString("""{hoge:fuga}""")

    assert((conf / "not" / "exists" exists) == false)

  }
  "Not exist path" should "get as Nones" in {
    val conf = TSConfigFactory.parseString("""{hoge:fuga}""")
    val v = conf / "not" / "exists"
    assert(v.get[Int] == None)
    assert(v.get[Long] == None)
    assert(v.get[String] == None)
  }

  "Not exist " should "be empty list" in {
    val conf = TSConfigFactory.parseString("""{hoge:fuga}""")
    val v = conf / "not" / "exists"

    assert(v.asList == Nil)
    assert(v.as[List[String]] == Nil)
    assert(v.as[Seq[Boolean]] == Seq())
    assert(v.as[Set[Long]] == Set())
    assert(v.as[Map[String,Int]] == Map())

  }

  "Wrong type path" should "return any value.(not exception)" in {
    val conf = TSConfigFactory.parseString("""{notList:fuga}""")

    try {
      (conf / "notList").asList[String]
      fail()
    }catch{
      case e : ConfigException.WrongType => {
        // ok
      }
    }
  }

  "Case class" should "set default values to not exist fields" in {
    val conf = TSConfigFactory.parseString("""{a:2121}""")

    val a = conf.as[ABC]
    assert(a == ABC(2121,"b",10.0))

  }

  "Case class" should "throw NoValueException if parameter is not enouph" in {
    val conf = TSConfigFactory.parseString("""{aaaa:2121}""")

    try {
      val a = conf.as[ABC]
      fail()
    }catch{
      case e : NoValueException => {
        assert(e.paramName == "a")
        // OK

      }
    }
  }

  "Reference" should "be resolved" in {
    val conf = TSConfigFactory.parseString(
      """
        |hoge = fuga,
        |aaa = ${hoge}
        |
      """.stripMargin)

    assert((conf / "aaa").asString == "fuga")
  }

}

case class ABC(a : Int , b : String = "b", c : Double = 10.0)
