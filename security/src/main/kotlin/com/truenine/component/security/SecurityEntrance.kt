package com.truenine.component.security

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class SecurityEntrance

// jwt签发者 jwt验签者
fun main(args: Array<String>) {
  runApplication<SecurityEntrance>(*args)
}
