package net.yan100.compose.depend.jvalid.controller

import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import net.yan100.compose.depend.jvalid.repositories.GetRepo
import net.yan100.compose.testtookit.SpringServletTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.net.URI

@EntityScan("net.yan100.compose.depend.jvalid")
@SpringServletTest
class ValidGroupTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @Autowired
  lateinit var fac: ValidatorFactory

  @Autowired
  lateinit var validator: Validator

  @Test
  fun `test jpa save entity`() {
    val ret = mockMvc.post(URI("/valid-test/post")) {
      param("name", "123")
      param("age", "123")
      param("id", "123")
    }.andExpect {
      status { isOk() }
    }.andReturn().response.contentAsString
    println(ret)
  }

  @Test
  fun `test get valid`() {
    println(fac)
    val ret = mockMvc.get(URI("/valid-test/get")) {
      param("name", "123")
      param("age", "123")
      param("id", "123")
    }
      .andExpect {
        status { isOk() }

      }.andReturn().response.contentAsString
    println(ret)
  }
}
