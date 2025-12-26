package io.github.truenine.composeserver.rds.flywaymigrationmysql8.flyway

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabaseMysqlContainer
import jakarta.annotation.Resource
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

/**
 * rm_presort_tree_struct stored procedure tests.
 *
 * Verifies the behavior and idempotency of the rm_presort_tree_struct stored procedure.
 */
@SpringBootTest
@Transactional
@Rollback
class RmPresortTreeStructMigrationTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_presort_tree_struct_table")
  }

  @Nested
  inner class RmPresortTreeStructTests {

    @Test
    fun `rm_presort_tree_struct should remove all presorted tree struct columns`() {
      // Create test table and add presorted tree struct
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_struct_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_struct_table')")

      // Verify columns exist
      val beforeColumns = getTableColumns("test_presort_tree_struct_table")
      val presortTreeColumns = listOf("rpi", "rln", "rrn", "nlv", "tgi")
      assertTrue(beforeColumns.containsAll(presortTreeColumns), "All presorted tree struct columns should be present")

      // Call rm_presort_tree_struct
      jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_struct_table')")

      // Verify columns are removed
      val afterColumns = getTableColumns("test_presort_tree_struct_table")
      presortTreeColumns.forEach { column -> assertTrue(!afterColumns.contains(column), "Column '$column' should not exist") }
      assertEquals(listOf("name"), afterColumns, "Only the original column should remain")
    }

    @Test
    fun `rm_presort_tree_struct should remove columns in correct order`() {
      // Create test table and add presorted tree struct
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_struct_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_struct_table')")

      // Call rm_presort_tree_struct
      jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_struct_table')")

      // Verify all columns are removed (order: tgi, nlv, rrn, rln, rpi)
      val afterColumns = getTableColumns("test_presort_tree_struct_table")
      assertEquals(listOf("name"), afterColumns, "Only the original column should remain")
    }

    @Test
    fun `rm_presort_tree_struct idempotency test`() {
      // Create test table and add presorted tree struct
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_struct_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_struct_table')")

      // First removal
      jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_struct_table')")
      val afterFirst = getTableColumns("test_presort_tree_struct_table")

      // Second removal (idempotency test)
      jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_struct_table')")
      val afterSecond = getTableColumns("test_presort_tree_struct_table")

      // Third removal (further verification)
      jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_struct_table')")
      val afterThird = getTableColumns("test_presort_tree_struct_table")

      // Verify idempotency
      assertEquals(afterFirst, afterSecond, "Columns should be the same after the second call")
      assertEquals(afterSecond, afterThird, "Columns should be the same after the third call")

      // Verify final state
      assertEquals(listOf("name"), afterThird, "Only the original column should remain")
    }

    @Test
    fun `combined operations idempotency test`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_struct_table(name VARCHAR(255))")

      // Repeatedly execute add and remove operations
      repeat(3) {
        jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_struct_table')")
        jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_struct_table')")
      }

      // Verify final state
      val finalColumns = getTableColumns("test_presort_tree_struct_table")
      assertEquals(listOf("name"), finalColumns, "Only the original column should remain")
    }
  }

  // Helper methods
  private fun getTableColumns(tableName: String): List<String> {
    return jdbcTemplate
      .queryForList(
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
      .filterNotNull()
  }
}
