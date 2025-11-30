package io.github.truenine.composeserver.rds.flywaymigrationpostgresql.flyway

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import jakarta.annotation.Resource
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
@Rollback
class AddTreeStructMigrationTest : IDatabasePostgresqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @Test
  fun `add_tree_struct should add columns correctly`() {
    val tableName = "test_table_add_fields"

    // Clean up existing table if present
    jdbcTemplate.execute("drop table if exists $tableName")

    // Create test table
    jdbcTemplate.execute("create table $tableName(id bigint primary key)")

    try {
      // Execute add_tree_struct function
      jdbcTemplate.execute("select add_tree_struct('$tableName')")

      // Verify that columns are added correctly
      val columns =
        jdbcTemplate
          .queryForList(
            """
            select column_name from information_schema.columns
            where table_name = '$tableName'
            """
              .trimIndent()
          )
          .map { it["column_name"] }

      val expected = listOf("rpi")
      assertTrue(columns.containsAll(expected), "Missing tree columns: " + (expected - columns))
    } finally {
      // Clean up test table
      jdbcTemplate.execute("drop table if exists $tableName")
    }
  }

  @Test
  fun `add_tree_struct idempotency test`() {
    val tableName = "test_table_idempotent"

    // Clean up existing table if present
    jdbcTemplate.execute("drop table if exists $tableName")

    // Create test table
    jdbcTemplate.execute("create table $tableName(id bigint primary key)")

    try {
      // Execute add_tree_struct function multiple times to test idempotency
      jdbcTemplate.execute("select add_tree_struct('$tableName')")
      jdbcTemplate.execute("select add_tree_struct('$tableName')")

      // Verify that columns are added correctly and without duplication
      val columns =
        jdbcTemplate
          .queryForList(
            """
            select column_name from information_schema.columns
            where table_name = '$tableName'
            """
              .trimIndent()
          )
          .map { it["column_name"] }

      val expected = listOf("rpi")
      assertTrue(columns.containsAll(expected), "add_tree_struct idempotency failed: " + (expected - columns))
    } finally {
      // Clean up test table
      jdbcTemplate.execute("drop table if exists $tableName")
    }
  }
}
