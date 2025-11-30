package io.github.truenine.composeserver.rds.flywaymigrationmysql8

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabaseMysqlContainer
import jakarta.annotation.Resource
import org.junit.jupiter.api.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * V3 tree-structure stored procedure tests.
 *
 * Verifies the behavior and idempotency of the add_tree_struct and rm_tree_struct stored procedures.
 */
@SpringBootTest
@Transactional
@Rollback
class V3TreeStructTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_tree_table")
  }

  @Nested
  inner class AddTreeStructTests {

    @Test
    fun `add_tree_struct should add rpi column`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_tree_table(name VARCHAR(255))")

      // Get initial column count
      val initialColumns = getTableColumns("test_tree_table")
      assertEquals(1, initialColumns.size, "Initial table should have exactly one column")

      // Call add_tree_struct
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_table')")

      // Verify column is added
      val afterColumns = getTableColumns("test_tree_table")
      assertTrue(afterColumns.contains("rpi"), "Column 'rpi' should exist")
      assertEquals(2, afterColumns.size, "There should be 2 columns")
    }

    @Test
    fun `add_tree_struct should create index for rpi column`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_tree_table(name VARCHAR(255))")

      // Call add_tree_struct
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_table')")

      // Verify index is created
      assertTrue(hasIndex("test_tree_table", "rpi_idx"), "Index rpi_idx should be created")
    }

    @Test
    fun `add_tree_struct idempotency test`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_tree_table(name VARCHAR(255))")

      // First call
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_table')")
      val afterFirst = getTableColumns("test_tree_table")

      // Second call (idempotency test)
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_table')")
      val afterSecond = getTableColumns("test_tree_table")

      // Third call (further verification)
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_table')")
      val afterThird = getTableColumns("test_tree_table")

      // Verify idempotency
      assertEquals(afterFirst, afterSecond, "Columns should be the same after the second call")
      assertEquals(afterSecond, afterThird, "Columns should be the same after the third call")

      // Verify final columns
      assertTrue(afterThird.contains("rpi"), "Column 'rpi' should exist")
      assertEquals(2, afterThird.size, "There should be 2 columns")
    }
  }

  @Nested
  inner class RmTreeStructTests {

    @Test
    fun `rm_tree_struct should remove rpi column`() {
      // Create test table and add tree struct
      jdbcTemplate.execute("CREATE TABLE test_tree_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_table')")

      // Verify column exists
      val beforeColumns = getTableColumns("test_tree_table")
      assertTrue(beforeColumns.contains("rpi"), "Column 'rpi' should exist")

      // Call rm_tree_struct
      jdbcTemplate.execute("CALL rm_tree_struct('test_tree_table')")

      // Verify column is removed
      val afterColumns = getTableColumns("test_tree_table")
      assertTrue(!afterColumns.contains("rpi"), "Column 'rpi' should not exist")
      assertEquals(1, afterColumns.size, "There should be exactly 1 column")
    }

    @Test
    fun `rm_tree_struct idempotency test`() {
      // Create test table and add tree struct
      jdbcTemplate.execute("CREATE TABLE test_tree_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_table')")

      // First removal
      jdbcTemplate.execute("CALL rm_tree_struct('test_tree_table')")
      val afterFirst = getTableColumns("test_tree_table")

      // Second removal (idempotency test)
      jdbcTemplate.execute("CALL rm_tree_struct('test_tree_table')")
      val afterSecond = getTableColumns("test_tree_table")

      // Third removal (further verification)
      jdbcTemplate.execute("CALL rm_tree_struct('test_tree_table')")
      val afterThird = getTableColumns("test_tree_table")

      // Verify idempotency
      assertEquals(afterFirst, afterSecond, "Columns should be the same after the second call")
      assertEquals(afterSecond, afterThird, "Columns should be the same after the third call")

      // Verify final state
      assertEquals(listOf("name"), afterThird, "Only the original column should remain")
    }
  }

  @Nested
  inner class CombinedOperationsTests {

    @Test
    fun `combined operations idempotency test`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_tree_table(name VARCHAR(255))")

      // Repeatedly execute add and remove operations
      repeat(3) {
        jdbcTemplate.execute("CALL add_tree_struct('test_tree_table')")
        jdbcTemplate.execute("CALL rm_tree_struct('test_tree_table')")
      }

      // Verify final state
      val finalColumns = getTableColumns("test_tree_table")
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
