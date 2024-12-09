package com.example

import net.yan100.compose.rds.jimmer.autoconfig.AutoConfigEntrance
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@Import(AutoConfigEntrance::class)
@SpringBootApplication
internal class TestEntrance {
  fun main(args: Array<String>) {
    runApplication<TestEntrance>(*args)
  }
}
