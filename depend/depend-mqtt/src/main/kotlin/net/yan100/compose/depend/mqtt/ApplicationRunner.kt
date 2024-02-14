package net.yan100.compose.depend.mqtt

import net.yan100.compose.core.CoreEntrance
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class ApplicationRunner

fun main(args: Array<String>) {
    SpringApplication.run(CoreEntrance::class.java, *args)
}
