package io.tn.rds

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class RdsRunner

fun main(args: Array<String>) {
  runApplication<RdsRunner>(*args)
}

