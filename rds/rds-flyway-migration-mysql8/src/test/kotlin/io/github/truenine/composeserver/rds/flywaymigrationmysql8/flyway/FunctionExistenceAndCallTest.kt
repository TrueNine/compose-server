package io.github.truenine.composeserver.rds.flywaymigrationmysql8.flyway

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabaseMysqlContainer
import jakarta.annotation.Resource
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertTrue

/**
 * 存储过程存在性和调用测试
 *
 * 测试所有存储过程是否正确定义并可调用
 */
@SpringBootTest
@Transactional
@Rollback
class FunctionExistenceAndCallTest : IDatabaseMysqlContainer {
  @Resource
  lateinit var jdbcTemplate: JdbcTemplate

  @Nested
  inner class ProcedureExistenceTests {

    @Test
    fun `所有存储过程都应该存在`() {
      val expectedProcedures = listOf(
        "ct_idx",
        "add_base_struct",
        "rm_base_struct",
        "add_tree_struct",
        "rm_tree_struct",
        "add_presort_tree_struct",
        "rm_presort_tree_struct",
        "all_to_nullable",
        "base_struct_to_jimmer_style"
      )

      expectedProcedures.forEach { procedureName ->
        val exists = procedureExists(procedureName)
        assertTrue(exists, "存储过程 $procedureName 应该存在")
      }
    }

    @Test
    fun `所有存储过程都应该可以正常调用`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS test_call_table(name VARCHAR(255))")

      // 测试所有存储过程调用
      try {
        jdbcTemplate.execute("CALL ct_idx('test_call_table', 'name')")
        jdbcTemplate.execute("CALL add_base_struct('test_call_table')")
        jdbcTemplate.execute("CALL add_tree_struct('test_call_table')")
        jdbcTemplate.execute("CALL add_presort_tree_struct('test_call_table')")
        jdbcTemplate.execute("CALL all_to_nullable('test_call_table')")
        jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_call_table')")
        jdbcTemplate.execute("CALL rm_presort_tree_struct('test_call_table')")
        jdbcTemplate.execute("CALL rm_tree_struct('test_call_table')")
        jdbcTemplate.execute("CALL rm_base_struct('test_call_table')")

        // 如果没有抛出异常，说明所有存储过程都可以正常调用
        assertTrue(true, "所有存储过程都应该可以正常调用")
      } catch (e: Exception) {
        throw AssertionError("存储过程调用失败: ${e.message}", e)
      } finally {
        // 清理测试表
        jdbcTemplate.execute("DROP TABLE IF EXISTS test_call_table")
      }
    }

    @Test
    fun `验证存储过程参数类型`() {
      // 创建测试表
      jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS test_param_table(name VARCHAR(255))")

      // 测试参数类型正确性（这些调用不应该抛出类型相关的异常）
      try {
        // 测试字符串参数
        jdbcTemplate.execute("CALL ct_idx('test_param_table', 'name')")
        jdbcTemplate.execute("CALL add_base_struct('test_param_table')")
        jdbcTemplate.execute("CALL rm_base_struct('test_param_table')")
        jdbcTemplate.execute("CALL add_tree_struct('test_param_table')")
        jdbcTemplate.execute("CALL rm_tree_struct('test_param_table')")
        jdbcTemplate.execute("CALL add_presort_tree_struct('test_param_table')")
        jdbcTemplate.execute("CALL rm_presort_tree_struct('test_param_table')")
        jdbcTemplate.execute("CALL all_to_nullable('test_param_table')")
        jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_param_table')")

        assertTrue(true, "所有存储过程参数类型都应该正确")
      } catch (e: Exception) {
        throw AssertionError("存储过程参数类型验证失败: ${e.message}", e)
      } finally {
        // 清理测试表
        jdbcTemplate.execute("DROP TABLE IF EXISTS test_param_table")
      }
    }
  }

  // 辅助方法
  private fun procedureExists(procedureName: String): Boolean {
    val count = jdbcTemplate.queryForObject(
      """
      SELECT COUNT(*) 
      FROM information_schema.routines 
      WHERE routine_schema = DATABASE() 
        AND routine_name = ? 
        AND routine_type = 'PROCEDURE'
      """
        .trimIndent(),
      Int::class.java,
      procedureName
    ) ?: 0
    return count > 0
  }
}
