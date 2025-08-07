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
 * V4 预排序树形结构存储过程测试
 *
 * 测试 add_presort_tree_struct 和 rm_presort_tree_struct 存储过程的功能和幂等性
 */
@SpringBootTest
@Transactional
@Rollback
class V4PresortTreeStructTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_presort_tree_table")
  }

  @Nested
  inner class AddPresortTreeStructTests {

    @Test
    fun `add_presort_tree_struct 应该添加所有预排序树字段`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_table(name VARCHAR(255))")

      // 获取初始列数
      val initialColumns = getTableColumns("test_presort_tree_table")
      assertEquals(1, initialColumns.size, "初始应该只有一个字段")

      // 调用 add_presort_tree_struct
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_table')")

      // 验证字段添加
      val afterColumns = getTableColumns("test_presort_tree_table")
      val expectedColumns = listOf("name", "rpi", "rln", "rrn", "nlv", "tgi")
      assertTrue(afterColumns.containsAll(expectedColumns), "应该包含所有预排序树字段")
      assertEquals(6, afterColumns.size, "应该有 6 个字段")
    }

    @Test
    fun `add_presort_tree_struct 应该为所有字段创建索引`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_table(name VARCHAR(255))")

      // 调用 add_presort_tree_struct
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_table')")

      // 验证索引创建
      assertTrue(hasIndex("test_presort_tree_table", "rpi_idx"), "应该创建 rpi_idx 索引")
      assertTrue(hasIndex("test_presort_tree_table", "rln_idx"), "应该创建 rln_idx 索引")
      assertTrue(hasIndex("test_presort_tree_table", "rrn_idx"), "应该创建 rrn_idx 索引")
      assertTrue(hasIndex("test_presort_tree_table", "nlv_idx"), "应该创建 nlv_idx 索引")
      assertTrue(hasIndex("test_presort_tree_table", "tgi_idx"), "应该创建 tgi_idx 索引")
    }

    @Test
    fun `add_presort_tree_struct 字段应该有正确的默认值`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_table(name VARCHAR(255))")

      // 调用 add_presort_tree_struct
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_table')")

      // 插入测试数据验证默认值
      jdbcTemplate.execute("INSERT INTO test_presort_tree_table(name) VALUES('test')")

      val result = jdbcTemplate.queryForMap("SELECT rpi, rln, rrn, nlv, tgi FROM test_presort_tree_table WHERE name = 'test'")

      assertEquals(null, result["rpi"], "rpi 默认值应该是 null")
      assertEquals(1L, result["rln"], "rln 默认值应该是 1")
      assertEquals(2L, result["rrn"], "rrn 默认值应该是 2")
      assertEquals(0, result["nlv"], "nlv 默认值应该是 0")
      assertEquals(null, result["tgi"], "tgi 默认值应该是 null")
    }

    @Test
    fun `add_presort_tree_struct 幂等性测试`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_table(name VARCHAR(255))")

      // 第一次调用
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_table')")
      val afterFirst = getTableColumns("test_presort_tree_table")

      // 第二次调用（幂等性测试）
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_table')")
      val afterSecond = getTableColumns("test_presort_tree_table")

      // 第三次调用（进一步验证）
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_table')")
      val afterThird = getTableColumns("test_presort_tree_table")

      // 验证幂等性
      assertEquals(afterFirst, afterSecond, "第二次调用后字段应该相同")
      assertEquals(afterSecond, afterThird, "第三次调用后字段应该相同")

      // 验证字段正确
      val expectedColumns = listOf("name", "rpi", "rln", "rrn", "nlv", "tgi")
      assertTrue(afterThird.containsAll(expectedColumns), "应该包含所有预排序树字段")
      assertEquals(6, afterThird.size, "应该有 6 个字段")
    }
  }

  @Nested
  inner class RmPresortTreeStructTests {

    @Test
    fun `rm_presort_tree_struct 应该移除所有预排序树字段`() {
      // 创建测试表并添加预排序树结构
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_table')")

      // 验证字段存在
      val beforeColumns = getTableColumns("test_presort_tree_table")
      assertTrue(beforeColumns.contains("rpi"), "应该包含 rpi 字段")
      assertTrue(beforeColumns.contains("rln"), "应该包含 rln 字段")
      assertTrue(beforeColumns.contains("rrn"), "应该包含 rrn 字段")
      assertTrue(beforeColumns.contains("nlv"), "应该包含 nlv 字段")
      assertTrue(beforeColumns.contains("tgi"), "应该包含 tgi 字段")

      // 调用 rm_presort_tree_struct
      jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_table')")

      // 验证字段移除
      val afterColumns = getTableColumns("test_presort_tree_table")
      assertTrue(!afterColumns.contains("rpi"), "不应该包含 rpi 字段")
      assertTrue(!afterColumns.contains("rln"), "不应该包含 rln 字段")
      assertTrue(!afterColumns.contains("rrn"), "不应该包含 rrn 字段")
      assertTrue(!afterColumns.contains("nlv"), "不应该包含 nlv 字段")
      assertTrue(!afterColumns.contains("tgi"), "不应该包含 tgi 字段")
      assertEquals(1, afterColumns.size, "应该只有 1 个字段")
    }

    @Test
    fun `rm_presort_tree_struct 幂等性测试`() {
      // 创建测试表并添加预排序树结构
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_table')")

      // 第一次移除
      jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_table')")
      val afterFirst = getTableColumns("test_presort_tree_table")

      // 第二次移除（幂等性测试）
      jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_table')")
      val afterSecond = getTableColumns("test_presort_tree_table")

      // 第三次移除（进一步验证）
      jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_table')")
      val afterThird = getTableColumns("test_presort_tree_table")

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
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_table(name VARCHAR(255))")

      // 重复执行添加和移除操作
      repeat(3) {
        jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_table')")
        jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_table')")
      }

      // 验证最终状态
      val finalColumns = getTableColumns("test_presort_tree_table")
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
