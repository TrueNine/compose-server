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
 * V5 字段可空化存储过程测试
 *
 * 测试 all_to_nullable 存储过程的功能和幂等性
 */
@SpringBootTest
@Transactional
@Rollback
class V5AllToNullableTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_nullable_table")
  }

  @Nested
  inner class AllToNullableTests {

    @Test
    fun `all_to_nullable 应该将非主键字段设置为可空`() {
      // 创建测试表，包含主键和非空字段
      jdbcTemplate.execute(
        """
        CREATE TABLE test_nullable_table(
          id BIGINT PRIMARY KEY,
          name VARCHAR(255) NOT NULL,
          age INT NOT NULL DEFAULT 18,
          email VARCHAR(255) NOT NULL
        )
        """
          .trimIndent()
      )

      // 验证初始状态
      val beforeNullables = getColumnNullability("test_nullable_table")
      assertEquals("NO", beforeNullables["name"], "name 字段初始应该是 NOT NULL")
      assertEquals("NO", beforeNullables["age"], "age 字段初始应该是 NOT NULL")
      assertEquals("NO", beforeNullables["email"], "email 字段初始应该是 NOT NULL")

      // 调用 all_to_nullable
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")

      // 验证字段变为可空
      val afterNullables = getColumnNullability("test_nullable_table")
      assertEquals("NO", afterNullables["id"], "主键字段应该保持 NOT NULL")
      assertEquals("YES", afterNullables["name"], "name 字段应该变为可空")
      assertEquals("YES", afterNullables["age"], "age 字段应该变为可空")
      assertEquals("YES", afterNullables["email"], "email 字段应该变为可空")
    }

    @Test
    fun `all_to_nullable 应该移除字段的默认值`() {
      // 创建测试表，包含默认值
      jdbcTemplate.execute(
        """
        CREATE TABLE test_nullable_table(
          id BIGINT PRIMARY KEY,
          name VARCHAR(255) NOT NULL DEFAULT 'default_name',
          age INT NOT NULL DEFAULT 18
        )
        """
          .trimIndent()
      )

      // 验证初始默认值
      val beforeDefaults = getColumnDefaults("test_nullable_table")
      assertTrue(beforeDefaults["name"]?.contains("default_name") == true, "name 字段应该有默认值")
      assertTrue(beforeDefaults["age"]?.contains("18") == true, "age 字段应该有默认值")

      // 调用 all_to_nullable
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")

      // 验证默认值被移除
      val afterDefaults = getColumnDefaults("test_nullable_table")
      assertEquals(null, afterDefaults["name"], "name 字段默认值应该被移除")
      assertEquals(null, afterDefaults["age"], "age 字段默认值应该被移除")
    }

    @Test
    fun `all_to_nullable 应该保留主键约束`() {
      // 创建测试表
      jdbcTemplate.execute(
        """
        CREATE TABLE test_nullable_table(
          id BIGINT PRIMARY KEY,
          name VARCHAR(255) NOT NULL
        )
        """
          .trimIndent()
      )

      // 调用 all_to_nullable
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")

      // 验证主键约束保持不变
      val nullables = getColumnNullability("test_nullable_table")
      assertEquals("NO", nullables["id"], "主键字段应该保持 NOT NULL")

      // 验证主键约束仍然存在
      val primaryKeys = getPrimaryKeyColumns("test_nullable_table")
      assertTrue(primaryKeys.contains("id"), "id 应该仍然是主键")
    }

    @Test
    fun `all_to_nullable 幂等性测试`() {
      // 创建测试表
      jdbcTemplate.execute(
        """
        CREATE TABLE test_nullable_table(
          id BIGINT PRIMARY KEY,
          name VARCHAR(255) NOT NULL DEFAULT 'test',
          age INT NOT NULL DEFAULT 25
        )
        """
          .trimIndent()
      )

      // 第一次调用
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")
      val afterFirst = getColumnNullability("test_nullable_table")

      // 第二次调用（幂等性测试）
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")
      val afterSecond = getColumnNullability("test_nullable_table")

      // 第三次调用（进一步验证）
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")
      val afterThird = getColumnNullability("test_nullable_table")

      // 验证幂等性
      assertEquals(afterFirst, afterSecond, "第二次调用后可空性应该相同")
      assertEquals(afterSecond, afterThird, "第三次调用后可空性应该相同")

      // 验证最终状态
      assertEquals("NO", afterThird["id"], "主键应该保持 NOT NULL")
      assertEquals("YES", afterThird["name"], "name 字段应该是可空")
      assertEquals("YES", afterThird["age"], "age 字段应该是可空")
    }

    @Test
    fun `all_to_nullable 应该处理复合主键`() {
      // 创建包含复合主键的测试表
      jdbcTemplate.execute(
        """
        CREATE TABLE test_nullable_table(
          id1 BIGINT NOT NULL,
          id2 BIGINT NOT NULL,
          name VARCHAR(255) NOT NULL,
          PRIMARY KEY(id1, id2)
        )
        """
          .trimIndent()
      )

      // 调用 all_to_nullable
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")

      // 验证复合主键字段保持 NOT NULL
      val nullables = getColumnNullability("test_nullable_table")
      assertEquals("NO", nullables["id1"], "复合主键字段 id1 应该保持 NOT NULL")
      assertEquals("NO", nullables["id2"], "复合主键字段 id2 应该保持 NOT NULL")
      assertEquals("YES", nullables["name"], "非主键字段 name 应该变为可空")
    }
  }

  // 辅助方法
  private fun getColumnNullability(tableName: String): Map<String, String> {
    return jdbcTemplate
      .queryForList(
        """
        SELECT column_name, is_nullable 
        FROM information_schema.columns 
        WHERE table_schema = DATABASE() AND table_name = ?
        """
          .trimIndent(),
        tableName,
      )
      .associate { it["column_name"] as String to it["is_nullable"] as String }
  }

  private fun getColumnDefaults(tableName: String): Map<String, String?> {
    return jdbcTemplate
      .queryForList(
        """
        SELECT column_name, column_default 
        FROM information_schema.columns 
        WHERE table_schema = DATABASE() AND table_name = ?
        """
          .trimIndent(),
        tableName,
      )
      .associate { it["column_name"] as String to it["column_default"] as String? }
  }

  private fun getPrimaryKeyColumns(tableName: String): List<String> {
    return jdbcTemplate.queryForList(
      """
      SELECT column_name
      FROM information_schema.key_column_usage
      WHERE table_schema = DATABASE()
        AND table_name = ?
        AND constraint_name = 'PRIMARY'
      """
        .trimIndent(),
      String::class.java,
      tableName,
    )
  }
}
