package net.yan100.compose.core

import net.yan100.compose.core.autoconfig.AutoConfigEntrance
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@Import(AutoConfigEntrance::class)
@SpringBootApplication
class CoreEntrance

fun main(args: Array<String>) {
    SpringApplication.run(CoreEntrance::class.java, *args)
}
