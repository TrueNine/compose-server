package net.yan100.compose.security

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SecurityEntrance

fun main(args: Array<String>) {
  runApplication<SecurityEntrance>(*args)
}
