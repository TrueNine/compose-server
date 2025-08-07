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
 * all_to_nullable 存储过程测试
 *
 * 测试 all_to_nullable 存储过程的功能和幂等性
 */
@SpringBootTest
@Transactional
@Rollback
class AllToNullableMigrationTest : IDatabaseMysqlContainer {
  @Resource
  lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_nullable_table")
  }

  @Nested
  inner class AllToNullableTests {

    @Test
    fun `all_to_nullable 应该将非主键字段设置为可空`() {
      // 创建有NOT NULL约束的测试表
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

      // 调用 all_to_nullable
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")

      // 验证非主键字段都变为可空
      val columnInfo = getColumnInfo("test_nullable_table")

      assertEquals("NO", columnInfo["id"]?.get("is_nullable"), "主键字段应该保持NOT NULL")
      assertEquals("YES", columnInfo["name"]?.get("is_nullable"), "name字段应该变为可空")
      assertEquals("YES", columnInfo["age"]?.get("is_nullable"), "age字段应该变为可空")
      assertEquals("YES", columnInfo["email"]?.get("is_nullable"), "email字段应该变为可空")
    }

    @Test
    fun `all_to_nullable 应该移除非主键字段的默认值`() {
      // 创建有默认值的测试表
      jdbcTemplate.execute(
        """
        CREATE TABLE test_nullable_table(
          id BIGINT PRIMARY KEY,
          name VARCHAR(255) DEFAULT 'default_name',
          age INT DEFAULT 18,
          status VARCHAR(20) DEFAULT 'active'
        )
        """
          .trimIndent()
      )

      // 调用 all_to_nullable
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")

      // 验证默认值被移除
      val columnInfo = getColumnInfo("test_nullable_table")

      assertTrue(columnInfo["name"]?.get("column_default") == null, "name字段的默认值应该被移除")
      assertTrue(columnInfo["age"]?.get("column_default") == null, "age字段的默认值应该被移除")
      assertTrue(columnInfo["status"]?.get("column_default") == null, "status字段的默认值应该被移除")
    }

    @Test
    fun `all_to_nullable 应该保持主键字段不变`() {
      // 创建测试表
      jdbcTemplate.execute(
        """
        CREATE TABLE test_nullable_table(
          id BIGINT PRIMARY KEY,
          user_id BIGINT NOT NULL,
          name VARCHAR(255) NOT NULL
        )
        """
          .trimIndent()
      )

      // 获取调用前的主键信息
      val beforeColumnInfo = getColumnInfo("test_nullable_table")

      // 调用 all_to_nullable
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")

      // 验证主键字段保持不变
      val afterColumnInfo = getColumnInfo("test_nullable_table")

      assertEquals(beforeColumnInfo["id"]?.get("is_nullable"), afterColumnInfo["id"]?.get("is_nullable"), "主键字段的可空性不应该改变")
      assertEquals(beforeColumnInfo["id"]?.get("column_key"), afterColumnInfo["id"]?.get("column_key"), "主键约束不应该改变")
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
      val afterFirst = getColumnInfo("test_nullable_table")

      // 第二次调用（幂等性测试）
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")
      val afterSecond = getColumnInfo("test_nullable_table")

      // 第三次调用（进一步验证）
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")
      val afterThird = getColumnInfo("test_nullable_table")

      // 验证幂等性
      assertEquals(afterFirst["name"]?.get("is_nullable"), afterSecond["name"]?.get("is_nullable"), "第二次调用后name字段可空性应该相同")
      assertEquals(afterSecond["name"]?.get("is_nullable"), afterThird["name"]?.get("is_nullable"), "第三次调用后name字段可空性应该相同")

      assertEquals(afterFirst["age"]?.get("is_nullable"), afterSecond["age"]?.get("is_nullable"), "第二次调用后age字段可空性应该相同")
      assertEquals(afterSecond["age"]?.get("is_nullable"), afterThird["age"]?.get("is_nullable"), "第三次调用后age字段可空性应该相同")

      // 验证最终状态
      assertEquals("YES", afterThird["name"]?.get("is_nullable"), "name字段应该是可空的")
      assertEquals("YES", afterThird["age"]?.get("is_nullable"), "age字段应该是可空的")
    }

    @Test
    fun `all_to_nullable 应该处理复合主键`() {
      // 创建有复合主键的测试表
      jdbcTemplate.execute(
        """
        CREATE TABLE test_nullable_table(
          id1 BIGINT NOT NULL,
          id2 BIGINT NOT NULL,
          name VARCHAR(255) NOT NULL,
          age INT NOT NULL,
          PRIMARY KEY (id1, id2)
        )
        """
          .trimIndent()
      )

      // 调用 all_to_nullable
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")

      // 验证复合主键字段保持NOT NULL，非主键字段变为可空
      val columnInfo = getColumnInfo("test_nullable_table")

      assertEquals("NO", columnInfo["id1"]?.get("is_nullable"), "复合主键字段id1应该保持NOT NULL")
      assertEquals("NO", columnInfo["id2"]?.get("is_nullable"), "复合主键字段id2应该保持NOT NULL")
      assertEquals("YES", columnInfo["name"]?.get("is_nullable"), "name字段应该变为可空")
      assertEquals("YES", columnInfo["age"]?.get("is_nullable"), "age字段应该变为可空")
    }

    @Test
    fun `all_to_nullable 应该处理已经为空的字段`() {
      // 创建已有可空字段的测试表
      jdbcTemplate.execute(
        """
        CREATE TABLE test_nullable_table(
          id BIGINT PRIMARY KEY,
          nullable_field VARCHAR(255),
          not_null_field VARCHAR(255) NOT NULL
        )
        """
          .trimIndent()
      )

      // 调用 all_to_nullable
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")

      // 验证已可空字段保持不变，NOT NULL字段变为可空
      val columnInfo = getColumnInfo("test_nullable_table")

      assertEquals("YES", columnInfo["nullable_field"]?.get("is_nullable"), "已可空字段应该保持可空")
      assertEquals("YES", columnInfo["not_null_field"]?.get("is_nullable"), "NOT NULL字段应该变为可空")
    }
  }

  // 辅助方法
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
