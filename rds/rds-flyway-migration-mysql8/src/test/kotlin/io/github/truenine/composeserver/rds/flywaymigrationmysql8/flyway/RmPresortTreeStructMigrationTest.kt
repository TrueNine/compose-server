package io.github.truenine.composeserver.rds.flywaymigrationmysql8.flyway

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabaseMysqlContainer
import jakarta.annotation.Resource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * rm_presort_tree_struct 存储过程测试
 *
 * 测试 rm_presort_tree_struct 存储过程的功能和幂等性
 */
@SpringBootTest
@Transactional
@Rollback
class RmPresortTreeStructMigrationTest : IDatabaseMysqlContainer {
  @Resource
  lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_presort_tree_struct_table")
  }

  @Nested
  inner class RmPresortTreeStructTests {

    @Test
    fun `rm_presort_tree_struct 应该移除所有预排序树结构字段`() {
      // 创建测试表并添加预排序树结构
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_struct_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_struct_table')")

      // 验证字段存在
      val beforeColumns = getTableColumns("test_presort_tree_struct_table")
      val presortTreeColumns = listOf("rpi", "rln", "rrn", "nlv", "tgi")
      assertTrue(beforeColumns.containsAll(presortTreeColumns), "应该包含所有预排序树结构字段")

      // 调用 rm_presort_tree_struct
      jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_struct_table')")

      // 验证字段移除
      val afterColumns = getTableColumns("test_presort_tree_struct_table")
      presortTreeColumns.forEach { column ->
        assertTrue(!afterColumns.contains(column), "不应该包含 $column 字段")
      }
      assertEquals(listOf("name"), afterColumns, "应该只剩下原始字段")
    }

    @Test
    fun `rm_presort_tree_struct 应该按正确顺序移除字段`() {
      // 创建测试表并添加预排序树结构
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_struct_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_struct_table')")

      // 调用 rm_presort_tree_struct
      jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_struct_table')")

      // 验证所有字段都被移除（顺序：tgi, nlv, rrn, rln, rpi）
      val afterColumns = getTableColumns("test_presort_tree_struct_table")
      assertEquals(listOf("name"), afterColumns, "应该只剩下原始字段")
    }

    @Test
    fun `rm_presort_tree_struct 幂等性测试`() {
      // 创建测试表并添加预排序树结构
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_struct_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_struct_table')")

      // 第一次移除
      jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_struct_table')")
      val afterFirst = getTableColumns("test_presort_tree_struct_table")

      // 第二次移除（幂等性测试）
      jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_struct_table')")
      val afterSecond = getTableColumns("test_presort_tree_struct_table")

      // 第三次移除（进一步验证）
      jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_struct_table')")
      val afterThird = getTableColumns("test_presort_tree_struct_table")

      // 验证幂等性
      assertEquals(afterFirst, afterSecond, "第二次调用后字段应该相同")
      assertEquals(afterSecond, afterThird, "第三次调用后字段应该相同")

      // 验证最终状态
      assertEquals(listOf("name"), afterThird, "应该只剩下原始字段")
    }

    @Test
    fun `组合操作幂等性测试`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_struct_table(name VARCHAR(255))")

      // 重复执行添加和移除操作
      repeat(3) {
        jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_struct_table')")
        jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_struct_table')")
      }

      // 验证最终状态
      val finalColumns = getTableColumns("test_presort_tree_struct_table")
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
}
