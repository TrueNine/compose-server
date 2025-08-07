package io.github.truenine.composeserver.rds.flywaymigrationmysql8.flyway

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabaseMysqlContainer
import jakarta.annotation.Resource
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

/**
 * add_tree_struct 存储过程测试
 *
 * 测试 add_tree_struct 和 rm_tree_struct 存储过程的功能和幂等性
 */
@SpringBootTest
@Transactional
@Rollback
class AddTreeStructMigrationTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_tree_struct_table")
  }

  @Nested
  inner class AddTreeStructTests {

    @Test
    fun `add_tree_struct 应该添加树结构字段`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_tree_struct_table(name VARCHAR(255))")

      // 调用 add_tree_struct
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_struct_table')")

      // 验证字段添加
      val columns = getTableColumns("test_tree_struct_table")
      assertTrue(columns.contains("rpi"), "应该包含 rpi 字段")

      // 验证字段类型
      val columnInfo = getColumnInfo("test_tree_struct_table")
      assertEquals("bigint", columnInfo["rpi"]?.get("data_type")?.toString()?.lowercase(), "rpi 应该是 bigint 类型")
      assertEquals("YES", columnInfo["rpi"]?.get("is_nullable"), "rpi 应该可以为空")
    }

    @Test
    fun `add_tree_struct 应该创建索引`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_tree_struct_table(name VARCHAR(255))")

      // 调用 add_tree_struct
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_struct_table')")

      // 验证索引创建
      assertTrue(hasIndex("test_tree_struct_table", "rpi_idx"), "应该创建 rpi_idx 索引")
    }

    @Test
    fun `add_tree_struct 幂等性测试`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_tree_struct_table(name VARCHAR(255))")

      // 第一次调用
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_struct_table')")
      val afterFirst = getTableColumns("test_tree_struct_table")

      // 第二次调用（幂等性测试）
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_struct_table')")
      val afterSecond = getTableColumns("test_tree_struct_table")

      // 第三次调用（进一步验证）
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_struct_table')")
      val afterThird = getTableColumns("test_tree_struct_table")

      // 验证幂等性
      assertEquals(afterFirst, afterSecond, "第二次调用后字段应该相同")
      assertEquals(afterSecond, afterThird, "第三次调用后字段应该相同")
      assertTrue(afterThird.contains("rpi"), "应该包含 rpi 字段")
    }
  }

  // 辅助方法
  private fun getTableColumns(tableName: String): List<String> {
    return jdbcTemplate.queryForList(
      """
      SELECT column_name 
      FROM information_schema.columns 
      WHERE table_schema = DATABASE() AND table_name = ?
      ORDER BY ordinal_position
      """
        .trimIndent(),
      String::class.java,
      tableName,
    )
  }

  private fun getColumnInfo(tableName: String): Map<String, Map<String, Any?>> {
    return jdbcTemplate
      .queryForList(
        """
        SELECT column_name, data_type, is_nullable, column_key, column_default
        FROM information_schema.columns 
        WHERE table_schema = DATABASE() AND table_name = ?
        """
          .trimIndent(),
        tableName,
      )
      .associate {
        it["column_name"] as String to
          mapOf("data_type" to it["data_type"], "is_nullable" to it["is_nullable"], "column_key" to it["column_key"], "column_default" to it["column_default"])
      }
  }

  private fun hasIndex(tableName: String, indexName: String): Boolean {
    val count =
      jdbcTemplate.queryForObject(
        """
        SELECT COUNT(*) 
        FROM information_schema.statistics 
        WHERE table_schema = DATABASE() 
          AND table_name = ? 
          AND index_name = ?
        """
          .trimIndent(),
        Int::class.java,
        tableName,
        indexName,
      ) ?: 0
    return count > 0
  }
}
