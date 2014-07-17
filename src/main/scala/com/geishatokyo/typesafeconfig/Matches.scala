package com.geishatokyo.typesafeconfig

/**
 * Created by takezoux2 on 2014/07/17.
 */

object Val {

  object string {

    def unapply(conf: TSConfig): Option[String] = {
      conf.get[String]
    }

  }

  object int{
    def unapply(conf : TSConfig) : Option[Int] = {
      conf.get[Int]
    }
  }

  object bool {
    def unapply(conf: TSConfig): Option[Boolean] = {
      conf.get[Boolean]
    }
  }

  object double {

    def unapply(conf: TSConfig): Option[Double] = {
      conf.get[Double]
    }
  }

}
