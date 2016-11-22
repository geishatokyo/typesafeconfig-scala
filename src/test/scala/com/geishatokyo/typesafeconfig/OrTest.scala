package com.geishatokyo.typesafeconfig

import org.scalatest.{Matchers, FlatSpec}

/**
  * Created by takezoux2 on 2016/11/22.
  */
class OrTest extends FlatSpec with Matchers{

  it should "fallback" in {
    val conf1 = TSConfigFactory.parseString(
      """
      | key1: {
      |   aaa : first
      | }
      | key3: {
      |   ddd : onlyInConf1
      | }
      | value1 : inConf1
      """.stripMargin)


    val conf2 = TSConfigFactory.parseString(
      """
        | key1: {
        |   aaa : second
        |   bbb : onlyInConf2
        | }
        | key2: {
        |   ccc : onlyInConf2
        | }
        | value1 : inConf2
      """.stripMargin)


    // conf1にkey1.aaaがあるので、conf1の値が取得される
    assert( (((conf1 / "key1") or (conf2 / "key1")) / "aaa" asString) == "first")
    // conf1にkey1.aaaが無いためconf2の値が取得される
    assert( (((conf1 / "key1") or (conf2 / "key1")) / "bbb" asString) == "onlyInConf2")


    // conf1にkey2.aaaが無いためconf2の値が取得される
    assert( (((conf1 / "key2") or (conf2 / "key2")) / "ccc" asString) == "onlyInConf2")

    // conf1にのみkey3.aaaがあるため、conf1の値が取得される
    assert( (((conf1 / "key3") or (conf2 / "key3")) / "ddd" asString) == "onlyInConf1")

    //　値
    assert( (((conf1 / "value1") or (conf2 / "value1")) asString) == "inConf1")

  }


}
