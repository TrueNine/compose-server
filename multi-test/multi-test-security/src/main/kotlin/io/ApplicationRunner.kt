package io

import com.truenine.component.security.annotations.EnableJwtIssuer
import com.truenine.component.security.annotations.EnableRestSecurity
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableJwtIssuer
@EnableRestSecurity
@SpringBootApplication
class ApplicationRunner

fun main(args: Array<String>) {
  runApplication<ApplicationRunner>(*args)
}
