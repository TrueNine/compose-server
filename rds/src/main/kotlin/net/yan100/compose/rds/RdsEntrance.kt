package net.yan100.compose.rds

import net.yan100.compose.rds.autoconfig.AutoConfigEntrance
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@Import(AutoConfigEntrance::class)
@SpringBootApplication
class RdsEntrance

fun main(args: Array<String>) {
  SpringApplication.run(RdsEntrance::class.java, *args)
}
