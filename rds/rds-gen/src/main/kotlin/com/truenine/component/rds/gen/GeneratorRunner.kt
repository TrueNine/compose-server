package com.truenine.component.rds.gen


open class GeneratorRunner

fun main(args: Array<String>) {
  GeneratorStarter().config {
    it.packageName("com.daojiatech.center")
      .author("TrueNine")
  }.run().run("kt")
}