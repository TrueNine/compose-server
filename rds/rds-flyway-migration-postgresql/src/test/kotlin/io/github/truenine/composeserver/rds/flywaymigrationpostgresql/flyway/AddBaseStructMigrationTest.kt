package io.github.truenine.composeserver.rds.flywaymigrationpostgresql.flyway

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import jakarta.annotation.Resource
import kotlin.test.assertEquals
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
class AddBaseStructMigrationTest : IDatabasePostgresqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanTables() {
    jdbcTemplate.execute("drop table if exists test_table")
    jdbcTemplate.execute("drop table if exists test_no_id")
  }

  @Test
  @Transactional
  fun `add_base_struct should add base struct columns correctly`() {
    jdbcTemplate.execute("create table test_table(id bigint primary key)")
    jdbcTemplate.execute("select add_base_struct('test_table')")
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
    val expectedColumns = listOf("id", "rlv", "crd", "mrd", "ldf")
    assertTrue(columns.containsAll(expectedColumns), "Missing columns: " + (expectedColumns - columns))
  }

  @Test
  @Transactional
  fun `add_base_struct should add id column and set it as primary key for table without id`() {
    jdbcTemplate.execute(
      """
      create table test_no_id(nick_name varchar(255), password_enc varchar(255));
      insert into test_no_id(nick_name, password_enc) values ('a', 'p1'), ('b', 'p2'), ('c', 'p3');
      """
        .trimIndent()
    )
    jdbcTemplate.execute("select add_base_struct('test_no_id')")
    val columns =
      jdbcTemplate
        .queryForList(
          """
          select column_name from information_schema.columns
          where table_name = 'test_no_id'
          """
            .trimIndent()
        )
        .map { it["column_name"] }
    assertTrue(columns.contains("id"), "id column was not automatically added")
    val pk =
      jdbcTemplate
        .queryForList(
          """
          select kcu.column_name from information_schema.table_constraints tc
          join information_schema.key_column_usage kcu on tc.constraint_name = kcu.constraint_name
          where tc.table_name = 'test_no_id' and tc.constraint_type = 'PRIMARY KEY'
          """
            .trimIndent()
        )
        .map { it["column_name"] }
    assertTrue(pk.contains("id"), "id column was not set as primary key")
    val ids = jdbcTemplate.queryForList("select id from test_no_id order by id").map { it["id"] }
    assertEquals(listOf(1L, 2L, 3L), ids, "id column was not auto-incremented starting from 0, actual: $ids")
  }

  @Test
  @Transactional
  fun `add_base_struct idempotency test`() {
    jdbcTemplate.execute("create table test_table(id bigint primary key)")
    jdbcTemplate.execute("select add_base_struct('test_table')")
    jdbcTemplate.execute("select add_base_struct('test_table')")
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
    val expectedColumns = listOf("id", "rlv", "crd", "mrd", "ldf")
    assertTrue(columns.containsAll(expectedColumns), "Idempotency failed: " + (expectedColumns - columns))
  }
}
