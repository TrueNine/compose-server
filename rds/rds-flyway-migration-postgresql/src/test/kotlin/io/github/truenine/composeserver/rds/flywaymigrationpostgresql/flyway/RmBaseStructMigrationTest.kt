package io.github.truenine.composeserver.rds.flywaymigrationpostgresql.flyway

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import jakarta.annotation.Resource
import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@Rollback
class RmBaseStructMigrationTest : IDatabasePostgresqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanTables() {
    jdbcTemplate.execute("drop table if exists test_table")
  }

  @Test
  @Transactional
  fun `rm_base_struct 应正确移除字段`() {
    jdbcTemplate.execute("create table test_table(nick_name varchar default null)")
    jdbcTemplate.execute("select add_base_struct('test_table')")
    jdbcTemplate.execute("select rm_base_struct('test_table')")
    val columns =
      jdbcTemplate
        .queryForList(
          """
          select column_name from information_schema.columns
          where table_name = 'test_table'
          """
            .trimIndent()
        )
        .map { it["column_name"] }
    assertEquals(listOf("nick_name"), columns, "rm_base_struct 未正确移除字段，当前字段: $columns")
  }

  @Test
  @Transactional
  fun `rm_base_struct 幂等性测试`() {
    jdbcTemplate.execute("create table test_table(nick_name varchar default null)")
    jdbcTemplate.execute("select add_base_struct('test_table')")
    jdbcTemplate.execute("select rm_base_struct('test_table')")
    jdbcTemplate.execute("select rm_base_struct('test_table')")
    val columns =
      jdbcTemplate
        .queryForList(
          """
          select column_name from information_schema.columns
          where table_name = 'test_table'
          """
            .trimIndent()
        )
        .map { it["column_name"] }
    assertEquals(listOf("nick_name"), columns, "rm_base_struct 幂等性失败，当前字段: $columns")
  }
}
