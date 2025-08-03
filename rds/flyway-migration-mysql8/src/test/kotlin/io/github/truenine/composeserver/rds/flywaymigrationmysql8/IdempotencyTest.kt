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
 * MySQL 存储过程幂等性测试套件
 *
 * 验证所有存储过程可以安全地重复执行，确保：
 * 1. 多次调用不会产生错误
 * 2. 重复调用不会改变最终结果状态
 * 3. 数据库对象状态保持一致
 */
@SpringBootTest
@Transactional
@Rollback
class IdempotencyTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    // 清理可能存在的测试表
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_idempotency_table")
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_index_table")
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_base_struct_table")
  }

  @Nested
  inner class AddBaseStructIdempotencyTests {

    @Test
    fun `add_base_struct 应该支持幂等调用 - 空表场景`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255))")

      // 获取初始状态
      val initialColumns = getTableColumns("test_base_struct_table")
      assertEquals(1, initialColumns.size, "初始表应该只有一个字段")
      assertTrue(initialColumns.contains("name"), "应该包含 name 字段")

      // 第一次调用 add_base_struct
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")
      val afterFirstCall = getTableColumns("test_base_struct_table")

      // 第二次调用 add_base_struct（幂等性测试）
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")
      val afterSecondCall = getTableColumns("test_base_struct_table")

      // 第三次调用 add_base_struct（进一步验证幂等性）
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")
      val afterThirdCall = getTableColumns("test_base_struct_table")

      // 验证所有调用后的结果一致
      assertEquals(afterFirstCall.size, afterSecondCall.size, "第二次调用后字段数量应该相同")
      assertEquals(afterSecondCall.size, afterThirdCall.size, "第三次调用后字段数量应该相同")
      assertEquals(afterFirstCall.sorted(), afterSecondCall.sorted(), "第二次调用后字段列表应该相同")
      assertEquals(afterSecondCall.sorted(), afterThirdCall.sorted(), "第三次调用后字段列表应该相同")

      // 验证基础结构字段都存在
      val expectedBaseColumns = listOf("id", "rlv", "crd", "mrd", "ldf", "name")
      assertTrue(afterThirdCall.containsAll(expectedBaseColumns), "应该包含所有基础结构字段")
      assertEquals(expectedBaseColumns.size, afterThirdCall.size, "字段数量应该正确")
    }

    @Test
    fun `add_base_struct 应该支持幂等调用 - 已有部分字段场景`() {
      // 创建已有部分基础字段的表
      jdbcTemplate.execute(
        """
        CREATE TABLE test_base_struct_table(
          id BIGINT PRIMARY KEY,
          name VARCHAR(255),
          rlv INT DEFAULT 0
        )
        """
          .trimIndent()
      )

      // 获取初始状态
      val initialColumns = getTableColumns("test_base_struct_table")
      assertEquals(3, initialColumns.size, "初始表应该有 3 个字段")

      // 多次调用 add_base_struct
      repeat(3) { jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')") }

      // 验证最终状态
      val finalColumns = getTableColumns("test_base_struct_table")
      val expectedColumns = listOf("id", "name", "rlv", "crd", "mrd", "ldf")
      assertTrue(finalColumns.containsAll(expectedColumns), "应该包含所有基础结构字段")
      assertEquals(expectedColumns.size, finalColumns.size, "字段数量应该正确")

      // 验证主键约束仍然存在
      val primaryKeyColumns = getPrimaryKeyColumns("test_base_struct_table")
      assertEquals(listOf("id"), primaryKeyColumns, "主键约束应该保持不变")
    }

    @Test
    fun `add_base_struct 应该支持幂等调用 - 完整基础结构已存在场景`() {
      // 创建已有完整基础结构的表
      jdbcTemplate.execute(
        """
        CREATE TABLE test_base_struct_table(
          id BIGINT PRIMARY KEY AUTO_INCREMENT,
          name VARCHAR(255),
          rlv INT DEFAULT 0,
          crd TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          mrd TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
          ldf TIMESTAMP NULL DEFAULT NULL
        )
        """
          .trimIndent()
      )

      // 获取初始状态
      val initialColumns = getTableColumns("test_base_struct_table")
      val initialColumnInfo = getDetailedColumnInfo("test_base_struct_table")

      // 多次调用 add_base_struct
      repeat(3) { jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')") }

      // 验证状态未改变
      val finalColumns = getTableColumns("test_base_struct_table")
      val finalColumnInfo = getDetailedColumnInfo("test_base_struct_table")

      assertEquals(initialColumns.sorted(), finalColumns.sorted(), "字段列表应该保持不变")
      assertEquals(initialColumnInfo.size, finalColumnInfo.size, "字段详细信息应该保持不变")
    }
  }

  @Nested
  inner class RmBaseStructIdempotencyTests {

    @Test
    fun `rm_base_struct 应该支持幂等调用 - 有基础结构场景`() {
      // 创建有完整基础结构的表
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")

      // 验证基础结构存在
      val initialColumns = getTableColumns("test_base_struct_table")
      assertTrue(initialColumns.contains("id"), "应该有 id 字段")
      assertTrue(initialColumns.contains("rlv"), "应该有 rlv 字段")

      // 第一次调用 rm_base_struct
      jdbcTemplate.execute("CALL rm_base_struct('test_base_struct_table')")
      val afterFirstCall = getTableColumns("test_base_struct_table")

      // 第二次调用 rm_base_struct（幂等性测试）
      jdbcTemplate.execute("CALL rm_base_struct('test_base_struct_table')")
      val afterSecondCall = getTableColumns("test_base_struct_table")

      // 第三次调用 rm_base_struct（进一步验证幂等性）
      jdbcTemplate.execute("CALL rm_base_struct('test_base_struct_table')")
      val afterThirdCall = getTableColumns("test_base_struct_table")

      // 验证所有调用后的结果一致
      assertEquals(afterFirstCall.sorted(), afterSecondCall.sorted(), "第二次调用后字段列表应该相同")
      assertEquals(afterSecondCall.sorted(), afterThirdCall.sorted(), "第三次调用后字段列表应该相同")

      // 验证只剩下原始字段
      assertEquals(listOf("name"), afterThirdCall, "应该只剩下原始字段")

      // 验证主键约束已移除
      val primaryKeyColumns = getPrimaryKeyColumns("test_base_struct_table")
      assertTrue(primaryKeyColumns.isEmpty(), "主键约束应该被移除")
    }

    @Test
    fun `rm_base_struct 应该支持幂等调用 - 无基础结构场景`() {
      // 创建没有基础结构的表
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255), age INT)")

      // 获取初始状态
      val initialColumns = getTableColumns("test_base_struct_table")
      assertEquals(2, initialColumns.size, "初始表应该有 2 个字段")

      // 多次调用 rm_base_struct（应该安全执行）
      repeat(3) { jdbcTemplate.execute("CALL rm_base_struct('test_base_struct_table')") }

      // 验证状态未改变
      val finalColumns = getTableColumns("test_base_struct_table")
      assertEquals(initialColumns.sorted(), finalColumns.sorted(), "字段列表应该保持不变")
      assertEquals(2, finalColumns.size, "字段数量应该保持不变")
    }
  }

  @Nested
  inner class CtIdxIdempotencyTests {

    @Test
    fun `ct_idx 应该支持幂等调用 - 列存在场景`() {
      // 创建测试表
      jdbcTemplate.execute(
        """
        CREATE TABLE test_index_table(
          id BIGINT PRIMARY KEY AUTO_INCREMENT,
          name VARCHAR(255),
          email VARCHAR(255),
          age INT
        )
        """
          .trimIndent()
      )

      // 获取初始索引状态
      val initialIndexes = getTableIndexes("test_index_table")

      // 第一次调用 ct_idx
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'name')")
      val afterFirstCall = getTableIndexes("test_index_table")

      // 第二次调用 ct_idx（幂等性测试）
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'name')")
      val afterSecondCall = getTableIndexes("test_index_table")

      // 第三次调用 ct_idx（进一步验证幂等性）
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'name')")
      val afterThirdCall = getTableIndexes("test_index_table")

      // 验证索引创建成功
      assertTrue(afterFirstCall.contains("name_idx"), "应该创建 name_idx 索引")

      // 验证幂等性 - 索引数量和名称保持一致
      assertEquals(afterFirstCall.size, afterSecondCall.size, "第二次调用后索引数量应该相同")
      assertEquals(afterSecondCall.size, afterThirdCall.size, "第三次调用后索引数量应该相同")
      assertEquals(afterFirstCall.sorted(), afterSecondCall.sorted(), "第二次调用后索引列表应该相同")
      assertEquals(afterSecondCall.sorted(), afterThirdCall.sorted(), "第三次调用后索引列表应该相同")

      // 验证只有一个 name_idx 索引
      val nameIndexCount = afterThirdCall.count { it == "name_idx" }
      assertEquals(1, nameIndexCount, "应该只有一个 name_idx 索引")
    }

    @Test
    fun `ct_idx 应该支持幂等调用 - 多个列场景`() {
      // 创建测试表
      jdbcTemplate.execute(
        """
        CREATE TABLE test_index_table(
          id BIGINT PRIMARY KEY AUTO_INCREMENT,
          name VARCHAR(255),
          email VARCHAR(255),
          age INT
        )
        """
          .trimIndent()
      )

      // 为多个列创建索引，每个列调用多次
      val columns = listOf("name", "email", "age")
      columns.forEach { column -> repeat(3) { jdbcTemplate.execute("CALL ct_idx('test_index_table', '$column')") } }

      // 验证所有索引都被创建且只有一个
      val finalIndexes = getTableIndexes("test_index_table")
      columns.forEach { column ->
        val expectedIndexName = "${column}_idx"
        assertTrue(finalIndexes.contains(expectedIndexName), "应该创建 $expectedIndexName 索引")
        val indexCount = finalIndexes.count { it == expectedIndexName }
        assertEquals(1, indexCount, "应该只有一个 $expectedIndexName 索引")
      }
    }

    @Test
    fun `ct_idx 应该支持幂等调用 - 列不存在场景`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_index_table(id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255))")

      // 获取初始索引状态
      val initialIndexes = getTableIndexes("test_index_table")

      // 多次尝试为不存在的列创建索引（应该安全执行）
      repeat(3) { jdbcTemplate.execute("CALL ct_idx('test_index_table', 'nonexistent_column')") }

      // 验证索引状态未改变
      val finalIndexes = getTableIndexes("test_index_table")
      assertEquals(initialIndexes.sorted(), finalIndexes.sorted(), "索引列表应该保持不变")

      // 验证没有为不存在的列创建索引
      val nonexistentIndexes = finalIndexes.filter { it.contains("nonexistent") }
      assertTrue(nonexistentIndexes.isEmpty(), "不应该为不存在的列创建索引")
    }

    @Test
    fun `ct_idx 应该支持幂等调用 - 索引已存在场景`() {
      // 创建测试表并手动创建索引
      jdbcTemplate.execute(
        """
        CREATE TABLE test_index_table(
          id BIGINT PRIMARY KEY AUTO_INCREMENT,
          name VARCHAR(255),
          INDEX name_idx (name)
        )
        """
          .trimIndent()
      )

      // 获取初始索引状态
      val initialIndexes = getTableIndexes("test_index_table")
      assertTrue(initialIndexes.contains("name_idx"), "索引应该已经存在")

      // 多次调用 ct_idx（应该检测到索引已存在）
      repeat(3) { jdbcTemplate.execute("CALL ct_idx('test_index_table', 'name')") }

      // 验证索引状态未改变
      val finalIndexes = getTableIndexes("test_index_table")
      assertEquals(initialIndexes.sorted(), finalIndexes.sorted(), "索引列表应该保持不变")

      // 验证只有一个 name_idx 索引
      val nameIndexCount = finalIndexes.count { it == "name_idx" }
      assertEquals(1, nameIndexCount, "应该只有一个 name_idx 索引")
    }
  }

  @Nested
  inner class CombinedIdempotencyTests {

    @Test
    fun `组合操作应该支持幂等调用`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_idempotency_table(name VARCHAR(255), description TEXT)")

      // 执行组合操作序列，每个操作重复多次
      repeat(3) {
        jdbcTemplate.execute("CALL add_base_struct('test_idempotency_table')")
        jdbcTemplate.execute("CALL ct_idx('test_idempotency_table', 'name')")
        jdbcTemplate.execute("CALL ct_idx('test_idempotency_table', 'rlv')")
        jdbcTemplate.execute("CALL ct_idx('test_idempotency_table', 'crd')")
      }

      // 验证最终状态
      val finalColumns = getTableColumns("test_idempotency_table")
      val finalIndexes = getTableIndexes("test_idempotency_table")

      // 验证字段正确
      val expectedColumns = listOf("id", "name", "description", "rlv", "crd", "mrd", "ldf")
      assertTrue(finalColumns.containsAll(expectedColumns), "应该包含所有预期字段")
      assertEquals(expectedColumns.size, finalColumns.size, "字段数量应该正确")

      // 验证索引正确
      val expectedIndexes = listOf("name_idx", "rlv_idx", "crd_idx")
      expectedIndexes.forEach { expectedIndex ->
        assertTrue(finalIndexes.contains(expectedIndex), "应该包含 $expectedIndex 索引")
        val indexCount = finalIndexes.count { it == expectedIndex }
        assertEquals(1, indexCount, "应该只有一个 $expectedIndex 索引")
      }
    }

    @Test
    fun `添加和移除操作应该支持幂等调用`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE test_idempotency_table(name VARCHAR(255))")

      // 多次添加基础结构
      repeat(3) { jdbcTemplate.execute("CALL add_base_struct('test_idempotency_table')") }

      // 验证添加成功
      val afterAdd = getTableColumns("test_idempotency_table")
      assertTrue(afterAdd.contains("id"), "应该有 id 字段")
      assertTrue(afterAdd.contains("rlv"), "应该有 rlv 字段")

      // 多次移除基础结构
      repeat(3) { jdbcTemplate.execute("CALL rm_base_struct('test_idempotency_table')") }

      // 验证移除成功
      val afterRemove = getTableColumns("test_idempotency_table")
      assertEquals(listOf("name"), afterRemove, "应该只剩下原始字段")

      // 再次多次添加基础结构
      repeat(3) { jdbcTemplate.execute("CALL add_base_struct('test_idempotency_table')") }

      // 验证再次添加成功
      val afterSecondAdd = getTableColumns("test_idempotency_table")
      val expectedColumns = listOf("id", "name", "rlv", "crd", "mrd", "ldf")
      assertTrue(afterSecondAdd.containsAll(expectedColumns), "应该包含所有基础结构字段")
    }
  }

  // 辅助方法：获取表的所有列名
  private fun getTableColumns(tableName: String): List<String> {
    return jdbcTemplate.queryForList(
      """
      SELECT column_name
      FROM information_schema.columns
      WHERE table_schema = DATABASE()
        AND table_name = ?
      ORDER BY ordinal_position
      """
        .trimIndent(),
      String::class.java,
      tableName,
    )
  }

  // 辅助方法：获取表的主键列
  private fun getPrimaryKeyColumns(tableName: String): List<String> {
    return jdbcTemplate.queryForList(
      """
      SELECT kcu.column_name
      FROM information_schema.table_constraints tc
      JOIN information_schema.key_column_usage kcu ON tc.constraint_name = kcu.constraint_name
        AND tc.table_schema = kcu.table_schema
        AND tc.table_name = kcu.table_name
      WHERE tc.table_schema = DATABASE()
        AND tc.table_name = ?
        AND tc.constraint_type = 'PRIMARY KEY'
      ORDER BY kcu.ordinal_position
      """
        .trimIndent(),
      String::class.java,
      tableName,
    )
  }

  // 辅助方法：获取表的所有索引名
  private fun getTableIndexes(tableName: String): List<String> {
    return jdbcTemplate.queryForList(
      """
      SELECT DISTINCT index_name
      FROM information_schema.statistics
      WHERE table_schema = DATABASE()
        AND table_name = ?
        AND index_name != 'PRIMARY'
      ORDER BY index_name
      """
        .trimIndent(),
      String::class.java,
      tableName,
    )
  }

  // 辅助方法：获取表的详细列信息
  private fun getDetailedColumnInfo(tableName: String): List<Map<String, Any?>> {
    return jdbcTemplate.queryForList(
      """
      SELECT column_name, data_type, is_nullable, column_default, column_key
      FROM information_schema.columns
      WHERE table_schema = DATABASE()
        AND table_name = ?
      ORDER BY ordinal_position
      """
        .trimIndent(),
      tableName,
    )
  }
}
