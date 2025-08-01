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
 * V2 基础结构存储过程测试
 *
 * 测试 add_base_struct 和 rm_base_struct 存储过程的功能和幂等性
 */
@SpringBootTest
@Transactional
@Rollback
class V2BaseStructTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_base_struct_table")
  }

  @Nested
  inner class AddBaseStructTests {

    @Test
    fun `add_base_struct 应该添加所有基础字段`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255))")

      // 获取初始列数
      val initialColumns = getTableColumns("test_base_struct_table")
      assertEquals(1, initialColumns.size, "初始应该只有一个字段")

      // 调用 add_base_struct
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")

      // 验证字段添加
      val afterColumns = getTableColumns("test_base_struct_table")
      val expectedColumns = listOf("id", "name", "rlv", "crd", "mrd", "ldf")
      assertTrue(afterColumns.containsAll(expectedColumns), "应该包含所有基础字段")
      assertEquals(6, afterColumns.size, "应该有 6 个字段")
    }

    @Test
    fun `add_base_struct 应该设置正确的字段类型和约束`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255))")

      // 调用 add_base_struct
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")

      // 验证字段类型和约束
      val columnInfo = getColumnInfo("test_base_struct_table")

      // 验证 id 字段
      assertEquals("bigint", columnInfo["id"]?.get("data_type")?.toString()?.lowercase(), "id 应该是 bigint 类型")
      assertEquals("NO", columnInfo["id"]?.get("is_nullable"), "id 应该是 NOT NULL")
      assertEquals("PRI", columnInfo["id"]?.get("column_key"), "id 应该是主键")

      // 验证 rlv 字段
      assertEquals("int", columnInfo["rlv"]?.get("data_type")?.toString()?.lowercase(), "rlv 应该是 int 类型")
      assertEquals("YES", columnInfo["rlv"]?.get("is_nullable"), "rlv 应该是可空")

      // 验证时间戳字段
      assertEquals("timestamp", columnInfo["crd"]?.get("data_type")?.toString()?.lowercase(), "crd 应该是 timestamp 类型")
      assertEquals("timestamp", columnInfo["mrd"]?.get("data_type")?.toString()?.lowercase(), "mrd 应该是 timestamp 类型")
      assertEquals("timestamp", columnInfo["ldf"]?.get("data_type")?.toString()?.lowercase(), "ldf 应该是 timestamp 类型")
    }

    @Test
    fun `add_base_struct 应该设置正确的默认值`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255))")

      // 调用 add_base_struct
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")

      // 插入测试数据验证默认值
      jdbcTemplate.execute("INSERT INTO test_base_struct_table(name) VALUES('test')")

      val result = jdbcTemplate.queryForMap("SELECT rlv, crd, mrd, ldf FROM test_base_struct_table WHERE name = 'test'")

      assertEquals(0, result["rlv"], "rlv 默认值应该是 0")
      assertTrue(result["crd"] != null, "crd 应该有默认值（当前时间戳）")
      assertTrue(result["mrd"] != null, "mrd 应该有默认值（当前时间戳）")
      assertEquals(null, result["ldf"], "ldf 默认值应该是 null")
    }

    @Test
    fun `add_base_struct 幂等性测试`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255))")

      // 第一次调用
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")
      val afterFirst = getTableColumns("test_base_struct_table")

      // 第二次调用（幂等性测试）
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")
      val afterSecond = getTableColumns("test_base_struct_table")

      // 第三次调用（进一步验证）
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")
      val afterThird = getTableColumns("test_base_struct_table")

      // 验证幂等性
      assertEquals(afterFirst, afterSecond, "第二次调用后字段应该相同")
      assertEquals(afterSecond, afterThird, "第三次调用后字段应该相同")

      // 验证字段正确
      val expectedColumns = listOf("id", "name", "rlv", "crd", "mrd", "ldf")
      assertTrue(afterThird.containsAll(expectedColumns), "应该包含所有基础字段")
      assertEquals(6, afterThird.size, "应该有 6 个字段")
    }
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
  }

  @Nested
  inner class CombinedOperationsTests {

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

    @Test
    fun `处理已有数据的表`() {
      // 创建测试表并插入数据
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255))")
      jdbcTemplate.execute("INSERT INTO test_base_struct_table(name) VALUES('existing_data')")

      // 调用 add_base_struct
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")

      // 验证现有数据仍然存在且有正确的默认值
      val result = jdbcTemplate.queryForMap("SELECT name, rlv, crd, mrd, ldf FROM test_base_struct_table WHERE name = 'existing_data'")

      assertEquals("existing_data", result["name"], "原有数据应该保持不变")
      assertEquals(0, result["rlv"], "rlv 应该有默认值")
      assertTrue(result["crd"] != null, "crd 应该有默认值")
      assertTrue(result["mrd"] != null, "mrd 应该有默认值")
      assertEquals(null, result["ldf"], "ldf 应该是 null")
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
}
