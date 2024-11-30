package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
internal class TestEntrance {
  fun main(args: Array<String>) {
    runApplication<TestEntrance>(*args)
  }
}
