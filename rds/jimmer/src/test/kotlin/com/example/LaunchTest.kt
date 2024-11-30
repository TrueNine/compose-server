package com.example

import jakarta.annotation.Resource
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringBootTest
class LaunchTest {
  lateinit var jdbcTemplate: JdbcTemplate @Resource set

  @BeforeTest
  fun setup() {
    assertNotNull(jdbcTemplate)
    jdbcTemplate.execute("select 1;")
  }

  @Test
  fun `ensure launch project`() {
    log.info("暂时无语法错误")
  }
}
