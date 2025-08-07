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
 * base_struct_to_jimmer_style 存储过程测试
 *
 * 测试 base_struct_to_jimmer_style 存储过程的功能和幂等性
 */
@SpringBootTest
@Transactional
@Rollback
class BaseStructToJimmerStyleMigrationTest : IDatabaseMysqlContainer {
  @Resource
  lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_jimmer_style_table")
  }

  @Nested
  inner class BaseStructToJimmerStyleTests {

    @Test
    fun `base_struct_to_jimmer_style 应该调整rlv字段为正确的类型和默认值`() {
      // 创建测试表并添加基础结构
      jdbcTemplate.execute("CREATE TABLE test_jimmer_style_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_base_struct('test_jimmer_style_table')")

      // 调用 base_struct_to_jimmer_style
      jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_jimmer_style_table')")

      // 验证 rlv 字段属性
      val columnInfo = getColumnInfo("test_jimmer_style_table")
      assertEquals("int", columnInfo["rlv"]?.get("data_type")?.toString()?.lowercase(), "rlv 应该是 int 类型")
      assertEquals("0", columnInfo["rlv"]?.get("column_default")?.toString(), "rlv 默认值应该是 0")
      assertEquals("NO", columnInfo["rlv"]?.get("is_nullable"), "rlv 应该是 NOT NULL")
    }

    @Test
    fun `base_struct_to_jimmer_style 应该将boolean类型的ldf转换为timestamp`() {
      // 创建有boolean ldf字段的测试表
      jdbcTemplate.execute("CREATE TABLE test_jimmer_style_table(name VARCHAR(255), ldf TINYINT(1) DEFAULT 0)")

      // 插入测试数据
      jdbcTemplate.execute("INSERT INTO test_jimmer_style_table(name, ldf) VALUES('test1', 1)")
      jdbcTemplate.execute("INSERT INTO test_jimmer_style_table(name, ldf) VALUES('test2', 0)")

      // 调用 base_struct_to_jimmer_style
      jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_jimmer_style_table')")

      // 验证 ldf 字段类型转换
      val columnInfo = getColumnInfo("test_jimmer_style_table")
      assertEquals("timestamp", columnInfo["ldf"]?.get("data_type")?.toString()?.lowercase(), "ldf 应该是 timestamp 类型")
      assertTrue(columnInfo["ldf"]?.get("column_default") == null, "ldf 默认值应该是 null")
      assertEquals("YES", columnInfo["ldf"]?.get("is_nullable"), "ldf 应该可以为空")

      // 验证数据转换正确（true -> timestamp, false/null -> null）
      val results = jdbcTemplate.queryForList("SELECT name, ldf FROM test_jimmer_style_table ORDER BY name")

      val test1Result = results.find { it["name"] == "test1" }
      val test2Result = results.find { it["name"] == "test2" }

      assertTrue(test1Result?.get("ldf") != null, "原来为true的记录应该有时间戳值")
      assertEquals(null, test2Result?.get("ldf"), "原来为false的记录应该为null")
    }

    @Test
    fun `base_struct_to_jimmer_style 应该将int类型的ldf转换为timestamp并清空数据`() {
      // 创建有int ldf字段的测试表
      jdbcTemplate.execute("CREATE TABLE test_jimmer_style_table(name VARCHAR(255), ldf INT DEFAULT 0)")

      // 插入测试数据
      jdbcTemplate.execute("INSERT INTO test_jimmer_style_table(name, ldf) VALUES('test1', 123)")
      jdbcTemplate.execute("INSERT INTO test_jimmer_style_table(name, ldf) VALUES('test2', 456)")

      // 调用 base_struct_to_jimmer_style
      jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_jimmer_style_table')")

      // 验证 ldf 字段类型转换
      val columnInfo = getColumnInfo("test_jimmer_style_table")
      assertEquals("timestamp", columnInfo["ldf"]?.get("data_type")?.toString()?.lowercase(), "ldf 应该是 timestamp 类型")
      assertTrue(columnInfo["ldf"]?.get("column_default") == null, "ldf 默认值应该是 null")

      // 验证所有数据都被清空为null
      val results = jdbcTemplate.queryForList("SELECT name, ldf FROM test_jimmer_style_table ORDER BY name")
      results.forEach { result ->
        assertEquals(null, result["ldf"], "所有 ldf 值都应该为null")
      }
    }

    @Test
    fun `base_struct_to_jimmer_style 应该保持timestamp类型的ldf不变`() {
      // 创建已有timestamp ldf字段的测试表
      jdbcTemplate.execute("CREATE TABLE test_jimmer_style_table(name VARCHAR(255), ldf TIMESTAMP DEFAULT CURRENT_TIMESTAMP)")

      // 插入测试数据
      jdbcTemplate.execute("INSERT INTO test_jimmer_style_table(name) VALUES('test1')")

      // 获取调用前的字段信息
      val beforeColumnInfo = getColumnInfo("test_jimmer_style_table")

      // 调用 base_struct_to_jimmer_style
      jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_jimmer_style_table')")

      // 验证 ldf 字段保持timestamp类型，但默认值被设置为null
      val afterColumnInfo = getColumnInfo("test_jimmer_style_table")
      assertEquals("timestamp", afterColumnInfo["ldf"]?.get("data_type")?.toString()?.lowercase(), "ldf 应该保持 timestamp 类型")
      assertTrue(afterColumnInfo["ldf"]?.get("column_default") == null, "ldf 默认值应该被设置为 null")
    }

    @Test
    fun `base_struct_to_jimmer_style 幂等性测试`() {
      // 创建测试表并添加基础结构
      jdbcTemplate.execute("CREATE TABLE test_jimmer_style_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_base_struct('test_jimmer_style_table')")

      // 第一次调用
      jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_jimmer_style_table')")
      val afterFirst = getColumnInfo("test_jimmer_style_table")

      // 第二次调用（幂等性测试）
      jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_jimmer_style_table')")
      val afterSecond = getColumnInfo("test_jimmer_style_table")

      // 第三次调用（进一步验证）
      jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_jimmer_style_table')")
      val afterThird = getColumnInfo("test_jimmer_style_table")

      // 验证幂等性
      assertEquals(afterFirst["rlv"]?.get("data_type"), afterSecond["rlv"]?.get("data_type"), "第二次调用后rlv类型应该相同")
      assertEquals(afterSecond["rlv"]?.get("data_type"), afterThird["rlv"]?.get("data_type"), "第三次调用后rlv类型应该相同")

      assertEquals(afterFirst["ldf"]?.get("data_type"), afterSecond["ldf"]?.get("data_type"), "第二次调用后ldf类型应该相同")
      assertEquals(afterSecond["ldf"]?.get("data_type"), afterThird["ldf"]?.get("data_type"), "第三次调用后ldf类型应该相同")

      // 验证最终状态
      assertEquals("int", afterThird["rlv"]?.get("data_type")?.toString()?.lowercase(), "rlv 应该是 int 类型")
      assertEquals("timestamp", afterThird["ldf"]?.get("data_type")?.toString()?.lowercase(), "ldf 应该是 timestamp 类型")
    }

    @Test
    fun `base_struct_to_jimmer_style 应该处理不存在rlv或ldf字段的表`() {
      // 创建没有基础字段的测试表
      jdbcTemplate.execute("CREATE TABLE test_jimmer_style_table(name VARCHAR(255), age INT)")

      // 调用 base_struct_to_jimmer_style 应该不产生错误
      jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_jimmer_style_table')")

      // 验证表结构没有变化
      val columns = getTableColumns("test_jimmer_style_table")
      assertEquals(listOf("name", "age"), columns, "表结构不应该发生变化")
    }

    @Test
    fun `base_struct_to_jimmer_style 完整流程测试`() {
      // 创建测试表并添加基础结构
      jdbcTemplate.execute("CREATE TABLE test_jimmer_style_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_base_struct('test_jimmer_style_table')")

      // 手动修改某些字段类型模拟不标准的状态
      jdbcTemplate.execute("ALTER TABLE test_jimmer_style_table MODIFY COLUMN ldf TINYINT(1) DEFAULT 0")

      // 插入测试数据
      jdbcTemplate.execute("INSERT INTO test_jimmer_style_table(id, name, ldf) VALUES(1, 'test', 1)")

      // 调用 base_struct_to_jimmer_style
      jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_jimmer_style_table')")

      // 验证所有字段都符合Jimmer标准
      val columnInfo = getColumnInfo("test_jimmer_style_table")

      // 验证 rlv 字段
      assertEquals("int", columnInfo["rlv"]?.get("data_type")?.toString()?.lowercase(), "rlv 应该是 int 类型")
      assertEquals("0", columnInfo["rlv"]?.get("column_default")?.toString(), "rlv 默认值应该是 0")
      assertEquals("NO", columnInfo["rlv"]?.get("is_nullable"), "rlv 应该是 NOT NULL")

      // 验证 ldf 字段
      assertEquals("timestamp", columnInfo["ldf"]?.get("data_type")?.toString()?.lowercase(), "ldf 应该是 timestamp 类型")
      assertTrue(columnInfo["ldf"]?.get("column_default") == null, "ldf 默认值应该是 null")
      assertEquals("YES", columnInfo["ldf"]?.get("is_nullable"), "ldf 应该可以为空")

      // 验证数据转换正确
      val result = jdbcTemplate.queryForMap("SELECT name, ldf FROM test_jimmer_style_table WHERE name = 'test'")
      assertTrue(result["ldf"] != null, "原来为true的ldf应该有时间戳值")
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
