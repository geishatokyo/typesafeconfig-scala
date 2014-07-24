package com.geishatokyo.typesafeconfig

import org.scalatest.{Matchers, FlatSpec}
import com.typesafe.config.ConfigException


/**
 * Created by takezoux2 on 2014/07/16.
 */
class AsTest extends FlatSpec with Matchers {

  "As" should "parse simple case class" in{

    val conf = TSConfigFactory.parseString(
      """
        |obj : {
        |  name : hoge,
        |  age : 20
        |}
      """.stripMargin)

    val simple = (conf / "obj").as[SimpleObject]

    assert(simple.name == "hoge")
    assert(simple.age == 20)

  }

  "As" should "parse nested case class" in {

    val conf = TSConfigFactory.parseString(
      """
        |obj : {
        |  id : 111,
        |  obj : {
        |    name : "aaa",
        |    age : 234
        |  },
        |  list : [{
        |    name : "bbb",
        |    age : 344
        |  },{
        |    name : "ccc",
        |    age : "32"
        |  }]
        |}
      """.stripMargin)

    val nested = (conf / "obj").as[NestedObject]

    assert(nested.id == 111)
    assert(nested.obj.name == "aaa")
    assert(nested.obj.age == 234)
    assert(nested.list.size == 2)
    assert(nested.list(0).name == "bbb")
    assert(nested.list(0).age == 344)
    assert(nested.list(1).name == "ccc")
    assert(nested.list(1).age == 32)

  }

  "Option" should "be mapped" in {
    val conf = TSConfigFactory.parseString(
      """
        |obj : {
        |  name : hoge,
        |  age : "20"
        |}
      """.stripMargin)

    val simple = (conf / "obj").as[Option[SimpleObject]]

    assert(simple == Some(SimpleObject("hoge",20)))
    assert((conf / "noKey").as[Option[SimpleObject]] == None)

    assert( (conf / "obj" / "age").as[Option[Int]] == Some(20))
    assert( (conf / "obj" / "ageee").as[Option[Int]] == None)
    assert( (conf / "obj" / "ageee").get[Int] == None)
    assert( (conf / "noKey" / "age").as[Option[Int]] == None)
    assert( (conf / "noKey" / "age").get[Int] == None)
  }
  "List" should "be mapped" in {

    val conf = TSConfigFactory.parseString(
      """
        |list : {
        |  weapon : 1,
        |  armor : [1,2,3]
        |  users : [{name : aaa,age : 20},{name : bbb,age : 11}]
        |}
      """.stripMargin)

    assert( (conf / "list" / "armor").asList[Int] == List(1,2,3) )
    assert( (conf / "list" / "armor").as[List[Int]] == List(1,2,3))

    // path does not exists
    assert( (conf / "noKey" / "armor").asList[Int] == List())
    assert( (conf / "list" / "armorrrr").asList[Int] == List())

    // Not list
    intercept[ConfigException.WrongType] {
      (conf / "list" / "weapon").asList[Int]
    }
    intercept[ConfigException.WrongType] {
      (conf / "list" / "weapon").as[List[Int]]
    }

    assert( (conf / "list" / "users").as[List[SimpleObject]] == List(SimpleObject("aaa",20),SimpleObject("bbb",11)))

  }


  "Map[String,_]" should "be mapped with key,name" in {

    // Map[String,_]の場合,
    // 変換先のクラスにname,keyいずれかのフィールドが存在する場合、
    // Mapのkeyにあたる文字列が設定される
    val conf = TSConfigFactory.parseString(
      """
        |db1 : {user : root,password : hoge}
        |db2 : {key:k2,name:n2,user : root,password : fuga}
        |db3 : {name : d3,user : root,password : fuga}
        |db4 : {key : d4,user : root,password : fuga}
      """.stripMargin)

    val dbs = conf.as[Map[String,DB]]

    assert(dbs.size == 4)
    assert(dbs("db1") == DB("db1","db1","root","hoge"))
    assert(dbs("db2") == DB("k2","n2","root","fuga"))
    assert(dbs("db3") == DB("db3","d3","root","fuga"))
    assert(dbs("db4") == DB("d4","db4","root","fuga"))


  }

  "Map[String,List[_]]" should "be mapped" in{
    val conf = TSConfigFactory.parseString(
      """
        |a : [a,b,c],
        |b : [hoge,fuga],
        |c : [aaa]
      """.stripMargin)

    val map = conf.asMapOf[List[String]]
    assert(map("a") == List("a","b","c"))
    assert(map("b") == List("hoge","fuga"))
    assert(map("c") == List("aaa"))


  }

}

case class DB(key : String,name : String,user : String, password : String)

case class SimpleObject(name : String, age : Int)

case class NestedObject(id : Long,obj : SimpleObject,list : List[SimpleObject])