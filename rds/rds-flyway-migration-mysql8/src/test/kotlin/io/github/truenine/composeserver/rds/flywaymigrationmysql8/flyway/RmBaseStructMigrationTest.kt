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
 * rm_base_struct 存储过程测试
 *
 * 测试 rm_base_struct 存储过程的功能和幂等性
 */
@SpringBootTest
@Transactional
@Rollback
class RmBaseStructMigrationTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_base_struct_table")
  }

  @Nested
  inner class RmBaseStructTests {

    @Test
    fun `rm_base_struct 应该移除所有基础字段`() {
      // 创建测试表并添加基础结构
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")

      // 验证字段存在
      val beforeColumns = getTableColumns("test_base_struct_table")
      assertTrue(beforeColumns.contains("id"), "应该包含 id 字段")
      assertTrue(beforeColumns.contains("rlv"), "应该包含 rlv 字段")

      // 调用 rm_base_struct
      jdbcTemplate.execute("CALL rm_base_struct('test_base_struct_table')")

      // 验证字段移除
      val afterColumns = getTableColumns("test_base_struct_table")
      assertTrue(!afterColumns.contains("id"), "不应该包含 id 字段")
      assertTrue(!afterColumns.contains("rlv"), "不应该包含 rlv 字段")
      assertTrue(!afterColumns.contains("crd"), "不应该包含 crd 字段")
      assertTrue(!afterColumns.contains("mrd"), "不应该包含 mrd 字段")
      assertTrue(!afterColumns.contains("ldf"), "不应该包含 ldf 字段")
      assertEquals(1, afterColumns.size, "应该只有 1 个字段")
    }

    @Test
    fun `rm_base_struct 幂等性测试`() {
      // 创建测试表并添加基础结构
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")

      // 第一次移除
      jdbcTemplate.execute("CALL rm_base_struct('test_base_struct_table')")
      val afterFirst = getTableColumns("test_base_struct_table")

      // 第二次移除（幂等性测试）
      jdbcTemplate.execute("CALL rm_base_struct('test_base_struct_table')")
      val afterSecond = getTableColumns("test_base_struct_table")

      // 第三次移除（进一步验证）
      jdbcTemplate.execute("CALL rm_base_struct('test_base_struct_table')")
      val afterThird = getTableColumns("test_base_struct_table")

      // 验证幂等性
      assertEquals(afterFirst, afterSecond, "第二次调用后字段应该相同")
      assertEquals(afterSecond, afterThird, "第三次调用后字段应该相同")

      // 验证最终状态
      assertEquals(listOf("name"), afterThird, "应该只剩下原始字段")
    }

    @Test
    fun `组合操作幂等性测试`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255))")

      // 重复执行添加和移除操作
      repeat(3) {
        jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")
        jdbcTemplate.execute("CALL rm_base_struct('test_base_struct_table')")
      }

      // 验证最终状态
      val finalColumns = getTableColumns("test_base_struct_table")
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
