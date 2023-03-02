package com.truenine.component.security

import com.truenine.component.security.annotations.EnableJwtServer
import com.truenine.component.security.jwt.JwtServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test


@SpringBootTest(classes = [SecurityEntrance::class])
class RunnerTest : AbstractTestNGSpringContextTests() {

  @Autowired
  private lateinit var jwt: JwtServer

  @Test
  fun run() {
    println(jwt)
  }
}
