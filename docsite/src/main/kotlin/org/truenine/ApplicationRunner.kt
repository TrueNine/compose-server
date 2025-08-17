package org.truenine

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication internal class ApplicationRunner

fun main(args: Array<String>) {
  runApplication<ApplicationRunner>(*args)
}
