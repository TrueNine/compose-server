package io.github.truenine.composeserver.rds.flywaymigrationpostgresql.flyway

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import jakarta.annotation.Resource
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@Rollback
class AddTreeStructMigrationTest : IDatabasePostgresqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @Test
  fun `add_tree_struct 应正确增加字段`() {
    val tableName = "test_table_add_fields"

    // 清理可能存在的表
    jdbcTemplate.execute("drop table if exists $tableName")

    // 创建测试表
    jdbcTemplate.execute("create table $tableName(id bigint primary key)")

    try {
      // 执行 add_tree_struct 函数
      jdbcTemplate.execute("select add_tree_struct('$tableName')")

      // 验证字段是否正确添加
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
      assertTrue(columns.containsAll(expected), "缺少 tree 字段: " + (expected - columns))
    } finally {
      // 清理测试表
      jdbcTemplate.execute("drop table if exists $tableName")
    }
  }

  @Test
  fun `add_tree_struct 幂等性测试`() {
    val tableName = "test_table_idempotent"

    // 清理可能存在的表
    jdbcTemplate.execute("drop table if exists $tableName")

    // 创建测试表
    jdbcTemplate.execute("create table $tableName(id bigint primary key)")

    try {
      // 多次执行 add_tree_struct 函数测试幂等性
      jdbcTemplate.execute("select add_tree_struct('$tableName')")
      jdbcTemplate.execute("select add_tree_struct('$tableName')")

      // 验证字段是否正确添加且没有重复
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
      assertTrue(columns.containsAll(expected), "add_tree_struct 幂等性失败: " + (expected - columns))
    } finally {
      // 清理测试表
      jdbcTemplate.execute("drop table if exists $tableName")
    }
  }
}
