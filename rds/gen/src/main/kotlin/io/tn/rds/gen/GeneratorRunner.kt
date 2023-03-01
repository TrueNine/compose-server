package io.tn.rds.gen

import io.tn.rds.gen.util.Gen


open class GeneratorRunner

fun main(args: Array<String>) {
  Gen().config {
    it.genPkg("io.tn.rds")
      .entityBaseClassName("io.tn.rds.base.BaseDao")
      .db("acin_db_v1")
      .dao("dao", "Dao")
      .repo("repo", "Repo")
      .author("TrueNine")
  }.run().run("kt")
}
