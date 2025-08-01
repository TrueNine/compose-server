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
 * V1 索引创建存储过程测试
 *
 * 测试 ct_idx 存储过程的功能和幂等性
 */
@SpringBootTest
@Transactional
@Rollback
class V1CtIdxTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_index_table")
  }

  @Nested
  inner class CtIdxTests {

    @Test
    fun `ct_idx 应该为存在的列创建索引`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_index_table(name VARCHAR(255), age INT)")

      // 验证初始没有索引
      assertTrue(!hasIndex("test_index_table", "name_idx"), "初始不应该有 name_idx 索引")

      // 调用 ct_idx 为 name 列创建索引
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'name')")

      // 验证索引创建成功
      assertTrue(hasIndex("test_index_table", "name_idx"), "应该创建 name_idx 索引")
    }

    @Test
    fun `ct_idx 应该为多个列创建不同的索引`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_index_table(name VARCHAR(255), age INT, email VARCHAR(255))")

      // 为多个列创建索引
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'name')")
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'age')")
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'email')")

      // 验证所有索引都创建成功
      assertTrue(hasIndex("test_index_table", "name_idx"), "应该创建 name_idx 索引")
      assertTrue(hasIndex("test_index_table", "age_idx"), "应该创建 age_idx 索引")
      assertTrue(hasIndex("test_index_table", "email_idx"), "应该创建 email_idx 索引")
    }

    @Test
    fun `ct_idx 不应该为不存在的列创建索引`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_index_table(name VARCHAR(255))")

      // 获取初始索引数量
      val initialIndexCount = getIndexCount("test_index_table")

      // 尝试为不存在的列创建索引
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'nonexistent_column')")

      // 验证索引数量没有变化
      val afterIndexCount = getIndexCount("test_index_table")
      assertEquals(initialIndexCount, afterIndexCount, "不应该为不存在的列创建索引")
      assertTrue(!hasIndex("test_index_table", "nonexistent_column_idx"), "不应该创建 nonexistent_column_idx 索引")
    }

    @Test
    fun `ct_idx 幂等性测试 - 重复调用不应该产生错误`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_index_table(name VARCHAR(255), age INT)")

      // 第一次调用
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'name')")
      val afterFirst = getIndexCount("test_index_table")
      assertTrue(hasIndex("test_index_table", "name_idx"), "第一次调用应该创建索引")

      // 第二次调用（幂等性测试）
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'name')")
      val afterSecond = getIndexCount("test_index_table")

      // 第三次调用（进一步验证）
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'name')")
      val afterThird = getIndexCount("test_index_table")

      // 验证幂等性
      assertEquals(afterFirst, afterSecond, "第二次调用后索引数量应该相同")
      assertEquals(afterSecond, afterThird, "第三次调用后索引数量应该相同")
      assertTrue(hasIndex("test_index_table", "name_idx"), "索引应该仍然存在")
    }

    @Test
    fun `ct_idx 应该正确处理索引命名规则`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_index_table(user_name VARCHAR(255), user_age INT)")

      // 为带下划线的列名创建索引
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'user_name')")
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'user_age')")

      // 验证索引命名正确
      assertTrue(hasIndex("test_index_table", "user_name_idx"), "应该创建 user_name_idx 索引")
      assertTrue(hasIndex("test_index_table", "user_age_idx"), "应该创建 user_age_idx 索引")
    }

    @Test
    fun `ct_idx 应该处理已存在的索引`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_index_table(name VARCHAR(255))")

      // 手动创建索引
      jdbcTemplate.execute("CREATE INDEX name_idx ON test_index_table(name)")

      // 获取索引数量
      val beforeCount = getIndexCount("test_index_table")
      assertTrue(hasIndex("test_index_table", "name_idx"), "索引应该已存在")

      // 调用 ct_idx
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'name')")

      // 验证索引数量没有变化
      val afterCount = getIndexCount("test_index_table")
      assertEquals(beforeCount, afterCount, "索引数量不应该变化")
      assertTrue(hasIndex("test_index_table", "name_idx"), "索引应该仍然存在")
    }

    @Test
    fun `ct_idx 批量操作测试`() {
      // 创建测试表
      jdbcTemplate.execute(
        """
        CREATE TABLE test_index_table(
          id BIGINT PRIMARY KEY,
          name VARCHAR(255),
          age INT,
          email VARCHAR(255),
          created_at TIMESTAMP
        )
        """
          .trimIndent()
      )

      // 批量创建索引
      val columns = listOf("name", "age", "email", "created_at")
      columns.forEach { column -> jdbcTemplate.execute("CALL ct_idx('test_index_table', '$column')") }

      // 验证所有索引都创建成功
      columns.forEach { column -> assertTrue(hasIndex("test_index_table", "${column}_idx"), "应该创建 ${column}_idx 索引") }

      // 重复执行验证幂等性
      columns.forEach { column -> jdbcTemplate.execute("CALL ct_idx('test_index_table', '$column')") }

      // 再次验证索引仍然存在
      columns.forEach { column -> assertTrue(hasIndex("test_index_table", "${column}_idx"), "索引 ${column}_idx 应该仍然存在") }
    }
  }

  // 辅助方法
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

  private fun getIndexCount(tableName: String): Int {
    return jdbcTemplate.queryForObject(
      """
      SELECT COUNT(DISTINCT index_name) 
      FROM information_schema.statistics 
      WHERE table_schema = DATABASE() AND table_name = ?
      """
        .trimIndent(),
      Int::class.java,
      tableName,
    ) ?: 0
  }
}
