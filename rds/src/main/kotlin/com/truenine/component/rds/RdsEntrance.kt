package com.truenine.component.rds

import com.truenine.component.rds.autoconfig.AutoConfigEntrance
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@Import(AutoConfigEntrance::class)
@SpringBootApplication
open class RdsEntrance

fun main(args: Array<String>) {
  runApplication<RdsEntrance>(*args)
}
