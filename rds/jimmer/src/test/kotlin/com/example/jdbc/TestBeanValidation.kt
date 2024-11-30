package com.example.jdbc

import net.yan100.compose.testtookit.log
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Configuration
class TestBeanValidation {

  @Bean
  fun ensureJdbcConnectionSuccess(jdbcTemplate: JdbcTemplate): () -> Int {
    log.info("test jdbc connect, template: {}", jdbcTemplate)
    assertNotNull(jdbcTemplate)
    val result = jdbcTemplate.query("select 1;") { r, _ -> r.getInt(1) }.firstOrNull()
    assertEquals(1, result)
    return { 1 }
  }

  @Bean
  fun ensureJimmerDemoDbCreated(jdbcTemplate: JdbcTemplate): () -> Int {
    log.info("valid jimmer test schema")
    val sql = """
        select *
        from book
        left join book_store on book.store_id = book_store.id;
    """.trimIndent()
    jdbcTemplate.query(sql) { rs, _ -> }
    return { 1 }
  }
}
