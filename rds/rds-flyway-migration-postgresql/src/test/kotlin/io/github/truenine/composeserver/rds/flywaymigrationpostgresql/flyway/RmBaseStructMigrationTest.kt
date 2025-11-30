package io.github.truenine.composeserver.rds.flywaymigrationpostgresql.flyway

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import jakarta.annotation.Resource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals

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
  fun `rm_base_struct should remove base struct columns correctly`() {
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
    assertEquals(listOf("nick_name"), columns, "rm_base_struct did not remove base struct columns correctly, current columns: $columns")
  }

  @Test
  @Transactional
  fun `rm_base_struct idempotency test`() {
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
    assertEquals(listOf("nick_name"), columns, "rm_base_struct idempotency failed, current columns: $columns")
  }
}
