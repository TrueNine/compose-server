package io.github.truenine.composeserver.depend.springdocopenapi

import io.github.truenine.composeserver.depend.springdocopenapi.autoconfig.AutoConfigEntrance
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.context.annotation.Import
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class]) @Import(AutoConfigEntrance::class) class TestApplication

@RestController
@RequestMapping("/test")
class TestController {

  @GetMapping("/hello")
  fun hello(): Map<String, String> {
    return mapOf("message" to "Hello, World!")
  }

  @GetMapping("/info")
  fun info(): Map<String, Any> {
    return mapOf("service" to "springdoc-openapi-test", "version" to "1.0.0", "timestamp" to System.currentTimeMillis())
  }
}
