package com.truenine.component.rds.gen

import com.truenine.component.rds.gen.util.Gen


open class GeneratorRunner

fun main(args: Array<String>) {
  Gen().config {
    it.genPkg("com.truenine.component.rds")
      .entityBaseClassName("com.truenine.component.rds.base.BaseDao")
      .db("acin_db_v1")
      .dao("dao", "Dao")
      .repo("repo", "Repo")
      .author("TrueNine")
  }.run().run("kt")
}
