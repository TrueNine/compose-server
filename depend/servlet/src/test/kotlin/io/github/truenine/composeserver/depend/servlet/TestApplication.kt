package io.github.truenine.composeserver.depend.servlet

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class]) class TestApplication

fun main(args: Array<String>) {
  runApplication<TestApplication>(*args)
}
