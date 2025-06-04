package net.yan100.compose.rds.flywaymigrationpostgresql.flyway

import jakarta.annotation.Resource
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import net.yan100.compose.testtoolkit.testcontainers.IDatabasePostgresqlContainer
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

  @Test
  @Transactional
  fun `add_base_struct 应正确增加字段`() {
    jdbcTemplate.execute("drop table if exists test_table")
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
    assertTrue(
      columns.containsAll(expectedColumns),
      "缺少字段: " + (expectedColumns - columns),
    )
  }

  @Test
  @Transactional
  fun `add_base_struct 应为无 id 且多行数据的表自动填充 id 并设为主键`() {
    jdbcTemplate.execute("drop table if exists test_no_id")
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
    assertTrue(columns.contains("id"), "未自动添加 id 字段")
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
    assertTrue(pk.contains("id"), "id 字段未设为主键")
    val ids =
      jdbcTemplate.queryForList("select id from test_no_id order by id").map {
        it["id"]
      }
    assertEquals(listOf(1L, 2L, 3L), ids, "id 字段未按 0 开始自增填充，实际: $ids")
  }

  @Test
  @Transactional
  fun `add_base_struct 幂等性测试`() {
    jdbcTemplate.execute("drop table if exists test_table")
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
    assertTrue(
      columns.containsAll(expectedColumns),
      "幂等性失败: " + (expectedColumns - columns),
    )
  }
}
