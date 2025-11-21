package io.github.truenine.composeserver.rds.flywaymigrationpostgresql.flyway

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import jakarta.annotation.Resource
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@Rollback
class AddPresortTreeStructMigrationTest : IDatabasePostgresqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanTables() {
    jdbcTemplate.execute("drop table if exists test_table")
  }

  @Test
  @Transactional
  fun `add_presort_tree_struct should add presort tree columns correctly`() {
    jdbcTemplate.execute("create table test_table(id bigint primary key)")
    jdbcTemplate.execute("select add_presort_tree_struct('test_table')")
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
    val expected = listOf("rpi", "rln", "rrn")
    assertTrue(columns.containsAll(expected), "Missing presort tree columns: " + (expected - columns))
  }

  @Test
  @Transactional
  fun `add_presort_tree_struct idempotency test`() {
    jdbcTemplate.execute("create table test_table(id bigint primary key)")
    jdbcTemplate.execute("select add_presort_tree_struct('test_table')")
    jdbcTemplate.execute("select add_presort_tree_struct('test_table')")
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
    val expected = listOf("rpi", "rln", "rrn")
    assertTrue(columns.containsAll(expected), "add_presort_tree_struct idempotency failed: " + (expected - columns))
  }
}
