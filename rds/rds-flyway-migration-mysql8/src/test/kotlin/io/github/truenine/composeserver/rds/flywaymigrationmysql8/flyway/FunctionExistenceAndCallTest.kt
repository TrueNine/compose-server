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
 * Stored procedure existence and invocation tests.
 *
 * Verifies that all stored procedures are defined correctly and can be invoked.
 */
@SpringBootTest
@Transactional
@Rollback
class FunctionExistenceAndCallTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @Nested
  inner class ProcedureExistenceTests {

    @Test
    fun `all stored procedures should exist`() {
      val expectedProcedures =
        listOf(
          "ct_idx",
          "add_base_struct",
          "rm_base_struct",
          "add_tree_struct",
          "rm_tree_struct",
          "add_presort_tree_struct",
          "rm_presort_tree_struct",
          "all_to_nullable",
          "base_struct_to_jimmer_style",
        )

      expectedProcedures.forEach { procedureName ->
        val exists = procedureExists(procedureName)
        assertTrue(exists, "Stored procedure $procedureName should exist")
      }
    }

    @Test
    fun `all stored procedures should be callable successfully`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS test_call_table(name VARCHAR(255))")

      // Test calling all stored procedures
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

        // If no exception is thrown, all stored procedures can be called successfully
        assertTrue(true, "All stored procedures should be callable successfully")
      } catch (e: Exception) {
        throw AssertionError("Stored procedure invocation failed: ${e.message}", e)
      } finally {
        // Clean up test table
        jdbcTemplate.execute("DROP TABLE IF EXISTS test_call_table")
      }
    }

    @Test
    fun `verify stored procedure parameter types`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS test_param_table(name VARCHAR(255))")

      // Verify argument types (these calls should not throw type-related exceptions)
      try {
        // Test string parameters
        jdbcTemplate.execute("CALL ct_idx('test_param_table', 'name')")
        jdbcTemplate.execute("CALL add_base_struct('test_param_table')")
        jdbcTemplate.execute("CALL rm_base_struct('test_param_table')")
        jdbcTemplate.execute("CALL add_tree_struct('test_param_table')")
        jdbcTemplate.execute("CALL rm_tree_struct('test_param_table')")
        jdbcTemplate.execute("CALL add_presort_tree_struct('test_param_table')")
        jdbcTemplate.execute("CALL rm_presort_tree_struct('test_param_table')")
        jdbcTemplate.execute("CALL all_to_nullable('test_param_table')")
        jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_param_table')")

        assertTrue(true, "All stored procedure parameter types should be correct")
      } catch (e: Exception) {
        throw AssertionError("Stored procedure parameter type validation failed: ${e.message}", e)
      } finally {
        // Clean up test table
        jdbcTemplate.execute("DROP TABLE IF EXISTS test_param_table")
      }
    }
  }

  // Helper methods
  private fun procedureExists(procedureName: String): Boolean {
    val count =
      jdbcTemplate.queryForObject(
        """
        SELECT COUNT(*) 
        FROM information_schema.routines 
        WHERE routine_schema = DATABASE() 
          AND routine_name = ? 
          AND routine_type = 'PROCEDURE'
        """
          .trimIndent(),
        Int::class.java,
        procedureName,
      ) ?: 0
    return count > 0
  }
}
