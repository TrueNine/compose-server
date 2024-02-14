package net.yan100.compose.depend.webclient

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class WebClientEntrance

fun main(args: Array<String>) {
    SpringApplication.run(WebClientEntrance::class.java, *args)
}
