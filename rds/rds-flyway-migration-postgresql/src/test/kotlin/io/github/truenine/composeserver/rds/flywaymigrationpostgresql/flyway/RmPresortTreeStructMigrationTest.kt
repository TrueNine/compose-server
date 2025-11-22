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
class RmPresortTreeStructMigrationTest : IDatabasePostgresqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanTables() {
    jdbcTemplate.execute("drop table if exists test_table")
  }

  @Test
  @Transactional
  fun `rm_presort_tree_struct should remove presort tree columns correctly`() {
    jdbcTemplate.execute("create table test_table(id bigint primary key)")
    jdbcTemplate.execute("select add_presort_tree_struct('test_table')")
    jdbcTemplate.execute("select rm_presort_tree_struct('test_table')")
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
    val removed = listOf("rpi", "rln", "rrn")
    assertTrue(removed.none { it in columns }, "Presort tree columns were not removed: " + (removed.filter { it in columns }))
  }

  @Test
  @Transactional
  fun `rm_presort_tree_struct idempotency and full column coverage test`() {
    jdbcTemplate.execute("create table test_table(id bigint primary key)")
    jdbcTemplate.execute("select add_presort_tree_struct('test_table')")
    jdbcTemplate.execute("select rm_presort_tree_struct('test_table')")
    jdbcTemplate.execute("select rm_presort_tree_struct('test_table')")
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
    val removed = listOf("rpi", "rln", "rrn")
    assertTrue(removed.none { it in columns }, "rm_presort_tree_struct idempotency failed or columns were not removed: " + (removed.filter { it in columns }))
  }
}
