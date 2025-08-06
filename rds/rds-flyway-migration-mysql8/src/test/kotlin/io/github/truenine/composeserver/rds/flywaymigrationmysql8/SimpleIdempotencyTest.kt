package io.github.truenine.composeserver.rds.flywaymigrationmysql8

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabaseMysqlContainer
import jakarta.annotation.Resource
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

/**
 * 简化的 MySQL 存储过程幂等性测试
 *
 * 验证核心存储过程的幂等性：
 * 1. add_base_struct 可以安全重复调用
 * 2. rm_base_struct 可以安全重复调用
 * 3. ct_idx 可以安全重复调用
 */
@SpringBootTest
@Transactional
@Rollback
class SimpleIdempotencyTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_simple_table")
  }

  @Test
  fun `add_base_struct 幂等性测试`() {
    // 创建测试表
    jdbcTemplate.execute("CREATE TABLE test_simple_table(name VARCHAR(255))")

    // 获取初始列数
    val initialCount = getColumnCount("test_simple_table")
    assertEquals(1, initialCount, "初始应该只有一个字段")

    // 第一次调用
    jdbcTemplate.execute("CALL add_base_struct('test_simple_table')")
    val afterFirst = getColumnCount("test_simple_table")

    // 第二次调用（幂等性测试）
    jdbcTemplate.execute("CALL add_base_struct('test_simple_table')")
    val afterSecond = getColumnCount("test_simple_table")

    // 第三次调用（进一步验证）
    jdbcTemplate.execute("CALL add_base_struct('test_simple_table')")
    val afterThird = getColumnCount("test_simple_table")

    // 验证幂等性
    assertEquals(afterFirst, afterSecond, "第二次调用后字段数应该相同")
    assertEquals(afterSecond, afterThird, "第三次调用后字段数应该相同")

    // 验证基础字段存在
    val columns = getTableColumns("test_simple_table")
    assertTrue(columns.contains("id"), "应该有 id 字段")
    assertTrue(columns.contains("rlv"), "应该有 rlv 字段")
    assertTrue(columns.contains("crd"), "应该有 crd 字段")
    assertTrue(columns.contains("mrd"), "应该有 mrd 字段")
    assertTrue(columns.contains("ldf"), "应该有 ldf 字段")
  }

  @Test
  fun `rm_base_struct 幂等性测试`() {
    // 创建测试表并添加基础结构
    jdbcTemplate.execute("CREATE TABLE test_simple_table(name VARCHAR(255))")
    jdbcTemplate.execute("CALL add_base_struct('test_simple_table')")

    // 验证基础结构存在
    val withBaseStruct = getColumnCount("test_simple_table")
    assertTrue(withBaseStruct > 1, "应该有基础结构字段")

    // 第一次移除
    jdbcTemplate.execute("CALL rm_base_struct('test_simple_table')")
    val afterFirst = getColumnCount("test_simple_table")

    // 第二次移除（幂等性测试）
    jdbcTemplate.execute("CALL rm_base_struct('test_simple_table')")
    val afterSecond = getColumnCount("test_simple_table")

    // 第三次移除（进一步验证）
    jdbcTemplate.execute("CALL rm_base_struct('test_simple_table')")
    val afterThird = getColumnCount("test_simple_table")

    // 验证幂等性
    assertEquals(afterFirst, afterSecond, "第二次调用后字段数应该相同")
    assertEquals(afterSecond, afterThird, "第三次调用后字段数应该相同")

    // 验证只剩下原始字段
    val columns = getTableColumns("test_simple_table")
    assertEquals(listOf("name"), columns, "应该只剩下原始字段")
  }

  @Test
  fun `ct_idx 幂等性测试`() {
    // 创建测试表
    jdbcTemplate.execute(
      """
      CREATE TABLE test_simple_table(
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        name VARCHAR(255),
        email VARCHAR(255)
      )
      """
        .trimIndent()
    )

    // 获取初始索引数
    val initialIndexCount = getIndexCount("test_simple_table")

    // 第一次创建索引
    jdbcTemplate.execute("CALL ct_idx('test_simple_table', 'name')")
    val afterFirst = getIndexCount("test_simple_table")

    // 第二次创建索引（幂等性测试）
    jdbcTemplate.execute("CALL ct_idx('test_simple_table', 'name')")
    val afterSecond = getIndexCount("test_simple_table")

    // 第三次创建索引（进一步验证）
    jdbcTemplate.execute("CALL ct_idx('test_simple_table', 'name')")
    val afterThird = getIndexCount("test_simple_table")

    // 验证幂等性
    assertEquals(afterFirst, afterSecond, "第二次调用后索引数应该相同")
    assertEquals(afterSecond, afterThird, "第三次调用后索引数应该相同")

    // 验证索引创建成功
    assertTrue(afterFirst > initialIndexCount, "应该创建了新索引")

    // 验证索引存在
    val hasNameIndex = hasIndex("test_simple_table", "name_idx")
    assertTrue(hasNameIndex, "应该有 name_idx 索引")
  }

  @Test
  fun `组合操作幂等性测试`() {
    // 创建测试表
    jdbcTemplate.execute("CREATE TABLE test_simple_table(name VARCHAR(255))")

    // 重复执行组合操作
    repeat(3) {
      jdbcTemplate.execute("CALL add_base_struct('test_simple_table')")
      jdbcTemplate.execute("CALL ct_idx('test_simple_table', 'name')")
      jdbcTemplate.execute("CALL ct_idx('test_simple_table', 'rlv')")
    }

    // 验证最终状态
    val columns = getTableColumns("test_simple_table")
    val expectedColumns = listOf("id", "name", "rlv", "crd", "mrd", "ldf")
    assertTrue(columns.containsAll(expectedColumns), "应该包含所有预期字段")

    // 验证索引
    assertTrue(hasIndex("test_simple_table", "name_idx"), "应该有 name_idx 索引")
    assertTrue(hasIndex("test_simple_table", "rlv_idx"), "应该有 rlv_idx 索引")
  }

  // 辅助方法
  private fun getColumnCount(tableName: String): Int {
    return jdbcTemplate.queryForObject(
      """
      SELECT COUNT(*) 
      FROM information_schema.columns 
      WHERE table_schema = DATABASE() AND table_name = ?
      """
        .trimIndent(),
      Int::class.java,
      tableName,
    ) ?: 0
  }

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
