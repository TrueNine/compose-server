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
 * V4 presorted tree-structure stored procedure tests.
 *
 * Verifies the behavior and idempotency of the
 * add_presort_tree_struct and rm_presort_tree_struct stored procedures.
 */
@SpringBootTest
@Transactional
@Rollback
class V4PresortTreeStructTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_presort_tree_table")
  }

  @Nested
  inner class AddPresortTreeStructTests {

    @Test
    fun `add_presort_tree_struct should add all presorted tree columns`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_table(name VARCHAR(255))")

      // Get initial column count
      val initialColumns = getTableColumns("test_presort_tree_table")
      assertEquals(1, initialColumns.size, "Initial table should have exactly one column")

      // Call add_presort_tree_struct
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_table')")

      // Verify that columns are added
      val afterColumns = getTableColumns("test_presort_tree_table")
      val expectedColumns = listOf("name", "rpi", "rln", "rrn", "nlv", "tgi")
      assertTrue(afterColumns.containsAll(expectedColumns), "All presorted tree columns should be present")
      assertEquals(6, afterColumns.size, "There should be 6 columns")
    }

    @Test
    fun `add_presort_tree_struct should create indexes for all columns`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_table(name VARCHAR(255))")

      // Call add_presort_tree_struct
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_table')")

      // Verify indexes are created
      assertTrue(hasIndex("test_presort_tree_table", "rpi_idx"), "Index rpi_idx should be created")
      assertTrue(hasIndex("test_presort_tree_table", "rln_idx"), "Index rln_idx should be created")
      assertTrue(hasIndex("test_presort_tree_table", "rrn_idx"), "Index rrn_idx should be created")
      assertTrue(hasIndex("test_presort_tree_table", "nlv_idx"), "Index nlv_idx should be created")
      assertTrue(hasIndex("test_presort_tree_table", "tgi_idx"), "Index tgi_idx should be created")
    }

    @Test
    fun `add_presort_tree_struct columns should have correct default values`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_table(name VARCHAR(255))")

      // Call add_presort_tree_struct
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_table')")

      // Insert test data to verify default values
      jdbcTemplate.execute("INSERT INTO test_presort_tree_table(name) VALUES('test')")

      val result = jdbcTemplate.queryForMap("SELECT rpi, rln, rrn, nlv, tgi FROM test_presort_tree_table WHERE name = 'test'")

      assertEquals(null, result["rpi"], "Default value of rpi should be null")
      assertEquals(1L, result["rln"], "Default value of rln should be 1")
      assertEquals(2L, result["rrn"], "Default value of rrn should be 2")
      assertEquals(0, result["nlv"], "Default value of nlv should be 0")
      assertEquals(null, result["tgi"], "Default value of tgi should be null")
    }

    @Test
    fun `add_presort_tree_struct idempotency test`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_table(name VARCHAR(255))")

      // First call
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_table')")
      val afterFirst = getTableColumns("test_presort_tree_table")

      // Second call (idempotency test)
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_table')")
      val afterSecond = getTableColumns("test_presort_tree_table")

      // Third call (further verification)
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_table')")
      val afterThird = getTableColumns("test_presort_tree_table")

      // Verify idempotency
      assertEquals(afterFirst, afterSecond, "Columns should be the same after the second call")
      assertEquals(afterSecond, afterThird, "Columns should be the same after the third call")

      // Verify final columns
      val expectedColumns = listOf("name", "rpi", "rln", "rrn", "nlv", "tgi")
      assertTrue(afterThird.containsAll(expectedColumns), "All presorted tree columns should be present")
      assertEquals(6, afterThird.size, "There should be 6 columns")
    }
  }

  @Nested
  inner class RmPresortTreeStructTests {

    @Test
    fun `rm_presort_tree_struct should remove all presorted tree columns`() {
      // Create test table and add presorted tree struct
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_table')")

      // Verify columns exist
      val beforeColumns = getTableColumns("test_presort_tree_table")
      assertTrue(beforeColumns.contains("rpi"), "Column 'rpi' should exist")
      assertTrue(beforeColumns.contains("rln"), "Column 'rln' should exist")
      assertTrue(beforeColumns.contains("rrn"), "Column 'rrn' should exist")
      assertTrue(beforeColumns.contains("nlv"), "Column 'nlv' should exist")
      assertTrue(beforeColumns.contains("tgi"), "Column 'tgi' should exist")

      // Call rm_presort_tree_struct
      jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_table')")

      // Verify columns are removed
      val afterColumns = getTableColumns("test_presort_tree_table")
      assertTrue(!afterColumns.contains("rpi"), "Column 'rpi' should not exist")
      assertTrue(!afterColumns.contains("rln"), "Column 'rln' should not exist")
      assertTrue(!afterColumns.contains("rrn"), "Column 'rrn' should not exist")
      assertTrue(!afterColumns.contains("nlv"), "Column 'nlv' should not exist")
      assertTrue(!afterColumns.contains("tgi"), "Column 'tgi' should not exist")
      assertEquals(1, afterColumns.size, "There should be exactly 1 column")
    }

    @Test
    fun `rm_presort_tree_struct idempotency test`() {
      // Create test table and add presorted tree struct
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_table')")

      // First removal
      jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_table')")
      val afterFirst = getTableColumns("test_presort_tree_table")

      // Second removal (idempotency test)
      jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_table')")
      val afterSecond = getTableColumns("test_presort_tree_table")

      // Third removal (further verification)
      jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_table')")
      val afterThird = getTableColumns("test_presort_tree_table")

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
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_table(name VARCHAR(255))")

      // Repeatedly execute add and remove operations
      repeat(3) {
        jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_table')")
        jdbcTemplate.execute("CALL rm_presort_tree_struct('test_presort_tree_table')")
      }

      // Verify final state
      val finalColumns = getTableColumns("test_presort_tree_table")
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
