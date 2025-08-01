package io.github.truenine.composeserver.rds.flywaymigrationmysql8

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
 * V3 树形结构存储过程测试
 *
 * 测试 add_tree_struct 和 rm_tree_struct 存储过程的功能和幂等性
 */
@SpringBootTest
@Transactional
@Rollback
class V3TreeStructTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_tree_table")
  }

  @Nested
  inner class AddTreeStructTests {

    @Test
    fun `add_tree_struct 应该添加 rpi 字段`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_tree_table(name VARCHAR(255))")

      // 获取初始列数
      val initialColumns = getTableColumns("test_tree_table")
      assertEquals(1, initialColumns.size, "初始应该只有一个字段")

      // 调用 add_tree_struct
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_table')")

      // 验证字段添加
      val afterColumns = getTableColumns("test_tree_table")
      assertTrue(afterColumns.contains("rpi"), "应该包含 rpi 字段")
      assertEquals(2, afterColumns.size, "应该有 2 个字段")
    }

    @Test
    fun `add_tree_struct 应该为 rpi 字段创建索引`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_tree_table(name VARCHAR(255))")

      // 调用 add_tree_struct
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_table')")

      // 验证索引创建
      assertTrue(hasIndex("test_tree_table", "rpi_idx"), "应该创建 rpi_idx 索引")
    }

    @Test
    fun `add_tree_struct 幂等性测试`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_tree_table(name VARCHAR(255))")

      // 第一次调用
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_table')")
      val afterFirst = getTableColumns("test_tree_table")

      // 第二次调用（幂等性测试）
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_table')")
      val afterSecond = getTableColumns("test_tree_table")

      // 第三次调用（进一步验证）
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_table')")
      val afterThird = getTableColumns("test_tree_table")

      // 验证幂等性
      assertEquals(afterFirst, afterSecond, "第二次调用后字段应该相同")
      assertEquals(afterSecond, afterThird, "第三次调用后字段应该相同")

      // 验证字段正确
      assertTrue(afterThird.contains("rpi"), "应该包含 rpi 字段")
      assertEquals(2, afterThird.size, "应该有 2 个字段")
    }
  }

  @Nested
  inner class RmTreeStructTests {

    @Test
    fun `rm_tree_struct 应该移除 rpi 字段`() {
      // 创建测试表并添加树形结构
      jdbcTemplate.execute("CREATE TABLE test_tree_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_table')")

      // 验证字段存在
      val beforeColumns = getTableColumns("test_tree_table")
      assertTrue(beforeColumns.contains("rpi"), "应该包含 rpi 字段")

      // 调用 rm_tree_struct
      jdbcTemplate.execute("CALL rm_tree_struct('test_tree_table')")

      // 验证字段移除
      val afterColumns = getTableColumns("test_tree_table")
      assertTrue(!afterColumns.contains("rpi"), "不应该包含 rpi 字段")
      assertEquals(1, afterColumns.size, "应该只有 1 个字段")
    }

    @Test
    fun `rm_tree_struct 幂等性测试`() {
      // 创建测试表并添加树形结构
      jdbcTemplate.execute("CREATE TABLE test_tree_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_table')")

      // 第一次移除
      jdbcTemplate.execute("CALL rm_tree_struct('test_tree_table')")
      val afterFirst = getTableColumns("test_tree_table")

      // 第二次移除（幂等性测试）
      jdbcTemplate.execute("CALL rm_tree_struct('test_tree_table')")
      val afterSecond = getTableColumns("test_tree_table")

      // 第三次移除（进一步验证）
      jdbcTemplate.execute("CALL rm_tree_struct('test_tree_table')")
      val afterThird = getTableColumns("test_tree_table")

      // 验证幂等性
      assertEquals(afterFirst, afterSecond, "第二次调用后字段应该相同")
      assertEquals(afterSecond, afterThird, "第三次调用后字段应该相同")

      // 验证最终状态
      assertEquals(listOf("name"), afterThird, "应该只剩下原始字段")
    }
  }

  @Nested
  inner class CombinedOperationsTests {

    @Test
    fun `组合操作幂等性测试`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_tree_table(name VARCHAR(255))")

      // 重复执行添加和移除操作
      repeat(3) {
        jdbcTemplate.execute("CALL add_tree_struct('test_tree_table')")
        jdbcTemplate.execute("CALL rm_tree_struct('test_tree_table')")
      }

      // 验证最终状态
      val finalColumns = getTableColumns("test_tree_table")
      assertEquals(listOf("name"), finalColumns, "应该只剩下原始字段")
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
