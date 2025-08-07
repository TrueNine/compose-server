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
 * add_presort_tree_struct 存储过程测试
 *
 * 测试 add_presort_tree_struct 和 rm_presort_tree_struct 存储过程的功能和幂等性
 */
@SpringBootTest
@Transactional
@Rollback
class AddPresortTreeStructMigrationTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_presort_tree_struct_table")
  }

  @Nested
  inner class AddPresortTreeStructTests {

    @Test
    fun `add_presort_tree_struct 应该添加所有预排序树结构字段`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_struct_table(name VARCHAR(255))")

      // 调用 add_presort_tree_struct
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_struct_table')")

      // 验证字段添加
      val columns = getTableColumns("test_presort_tree_struct_table")
      val expectedColumns = listOf("rpi", "rln", "rrn", "nlv", "tgi")
      assertTrue(columns.containsAll(expectedColumns), "应该包含所有预排序树结构字段")
    }

    @Test
    fun `add_presort_tree_struct 应该设置正确的字段类型和默认值`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_struct_table(name VARCHAR(255))")

      // 调用 add_presort_tree_struct
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_struct_table')")

      // 验证字段类型和默认值
      val columnInfo = getColumnInfo("test_presort_tree_struct_table")

      // 验证 rpi 字段
      assertEquals("bigint", columnInfo["rpi"]?.get("data_type")?.toString()?.lowercase(), "rpi 应该是 bigint 类型")
      assertEquals("YES", columnInfo["rpi"]?.get("is_nullable"), "rpi 应该可以为空")

      // 验证 rln 字段
      assertEquals("bigint", columnInfo["rln"]?.get("data_type")?.toString()?.lowercase(), "rln 应该是 bigint 类型")
      assertEquals("1", columnInfo["rln"]?.get("column_default")?.toString(), "rln 默认值应该是 1")

      // 验证 rrn 字段
      assertEquals("bigint", columnInfo["rrn"]?.get("data_type")?.toString()?.lowercase(), "rrn 应该是 bigint 类型")
      assertEquals("2", columnInfo["rrn"]?.get("column_default")?.toString(), "rrn 默认值应该是 2")

      // 验证 nlv 字段
      assertEquals("int", columnInfo["nlv"]?.get("data_type")?.toString()?.lowercase(), "nlv 应该是 int 类型")
      assertEquals("0", columnInfo["nlv"]?.get("column_default")?.toString(), "nlv 默认值应该是 0")

      // 验证 tgi 字段
      assertEquals("varchar", columnInfo["tgi"]?.get("data_type")?.toString()?.lowercase(), "tgi 应该是 varchar 类型")
      assertEquals("YES", columnInfo["tgi"]?.get("is_nullable"), "tgi 应该可以为空")
    }

    @Test
    fun `add_presort_tree_struct 应该创建所有字段的索引`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_struct_table(name VARCHAR(255))")

      // 调用 add_presort_tree_struct
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_struct_table')")

      // 验证索引创建
      assertTrue(hasIndex("test_presort_tree_struct_table", "rpi_idx"), "应该创建 rpi_idx 索引")
      assertTrue(hasIndex("test_presort_tree_struct_table", "rln_idx"), "应该创建 rln_idx 索引")
      assertTrue(hasIndex("test_presort_tree_struct_table", "rrn_idx"), "应该创建 rrn_idx 索引")
      assertTrue(hasIndex("test_presort_tree_struct_table", "nlv_idx"), "应该创建 nlv_idx 索引")
      assertTrue(hasIndex("test_presort_tree_struct_table", "tgi_idx"), "应该创建 tgi_idx 索引")
    }

    @Test
    fun `add_presort_tree_struct 幂等性测试`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_struct_table(name VARCHAR(255))")

      // 第一次调用
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_struct_table')")
      val afterFirst = getTableColumns("test_presort_tree_struct_table")

      // 第二次调用（幂等性测试）
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_struct_table')")
      val afterSecond = getTableColumns("test_presort_tree_struct_table")

      // 第三次调用（进一步验证）
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_struct_table')")
      val afterThird = getTableColumns("test_presort_tree_struct_table")

      // 验证幂等性
      assertEquals(afterFirst, afterSecond, "第二次调用后字段应该相同")
      assertEquals(afterSecond, afterThird, "第三次调用后字段应该相同")

      val expectedColumns = listOf("rpi", "rln", "rrn", "nlv", "tgi")
      assertTrue(afterThird.containsAll(expectedColumns), "应该包含所有预排序树结构字段")
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
