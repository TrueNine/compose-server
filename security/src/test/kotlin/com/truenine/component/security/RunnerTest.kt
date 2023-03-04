package com.truenine.component.security

import com.truenine.component.security.jwt.JwtVerifier
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test


@SpringBootTest(classes = [SecurityEntrance::class])
class RunnerTest : AbstractTestNGSpringContextTests() {

  @Autowired
  private lateinit var jwt: JwtVerifier

  @Test
  fun run() {
    println(jwt)
  }
}
