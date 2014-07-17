package com.geishatokyo.typesafeconfig

import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by takezoux2 on 2014/07/17.
 */
class PatternMatchingTest extends FlatSpec with Matchers {

  "TSConfig" should "match" in {

    val conf = TSConfigFactory.parseString(
      """
       | int : 1,
       | string:hoge,
       | boolean: true
       | double : 2.0
      """.stripMargin)

    conf / "string" match{
      case Val.string(v) => assert(v == "hoge")
      case _ => fail("Must match")
    }
    conf / "int" match{
      case Val.int(v) => assert(v == 1)
      case _ => fail("Must match")
    }
    conf / "boolean" match{
      case Val.bool(v) => assert(v == true)
      case _ => fail("Must match")
    }
    conf / "double" match{
      case Val.double(v) => assert(v == 2.0)
      case _ => fail("Must match")
    }

    conf / "noKey" match{
      case Val.string(v) => fail("Never match")
      case _ => {
        // Must reach here
        assert(true)
      }

    }

  }

}
