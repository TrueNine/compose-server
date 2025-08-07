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
 * add_base_struct 存储过程测试
 *
 * 测试 add_base_struct 和 rm_base_struct 存储过程的功能和幂等性
 */
@SpringBootTest
@Transactional
@Rollback
class AddBaseStructMigrationTest : IDatabaseMysqlContainer {
  @Resource
  lateinit var jdbcTemplate: JdbcTemplate

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
      assertEquals("NO", columnInfo["rlv"]?.get("is_nullable"), "rlv 应该不能为空")

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

      // 插入测试数据验证默认值（需要提供 id 值，因为 id 字段没有 AUTO_INCREMENT）
      jdbcTemplate.execute("INSERT INTO test_base_struct_table(id, name) VALUES(1, 'test')")

      val result = jdbcTemplate.queryForMap("SELECT rlv, crd, mrd, ldf FROM test_base_struct_table WHERE name = 'test'")

      assertEquals(0, result["rlv"], "rlv 默认值应该是 0")
      assertTrue(result["crd"] != null, "crd 应该有默认值（当前时间戳）")
      assertEquals(null, result["mrd"], "mrd 默认值应该是 null")
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
      assertEquals(null, result["mrd"], "mrd 默认值应该是 null")
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
