package net.yan100.compose.depend.webclient

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebClientEntrance

fun main(args: Array<String>) {
  runApplication<WebClientEntrance>(*args)
}
