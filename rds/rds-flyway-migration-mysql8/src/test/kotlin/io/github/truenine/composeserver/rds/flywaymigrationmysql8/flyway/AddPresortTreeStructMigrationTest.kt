package io.github.truenine.composeserver.rds.flywaymigrationmysql8.flyway

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
 * add_presort_tree_struct stored procedure tests.
 *
 * Verifies the behavior and idempotency of the add_presort_tree_struct and rm_presort_tree_struct stored procedures.
 */
@SpringBootTest
@Transactional
@Rollback
class AddPresortTreeStructMigrationTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_presort_tree_struct_table")
  }

  @Nested
  inner class AddPresortTreeStructTests {

    @Test
    fun `add_presort_tree_struct should add all presorted tree struct columns`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_struct_table(name VARCHAR(255))")

      // Call add_presort_tree_struct
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_struct_table')")

      // Verify columns are added
      val columns = getTableColumns("test_presort_tree_struct_table")
      val expectedColumns = listOf("rpi", "rln", "rrn", "nlv", "tgi")
      assertTrue(columns.containsAll(expectedColumns), "All presorted tree struct columns should be present")
    }

    @Test
    fun `add_presort_tree_struct should set correct column types and default values`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_struct_table(name VARCHAR(255))")

      // Call add_presort_tree_struct
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_struct_table')")

      // Verify column types and default values
      val columnInfo = getColumnInfo("test_presort_tree_struct_table")

      // Verify rpi column
      assertEquals("bigint", columnInfo["rpi"]?.get("data_type")?.toString()?.lowercase(), "rpi should be of type BIGINT")
      assertEquals("YES", columnInfo["rpi"]?.get("is_nullable"), "rpi should be nullable")

      // Verify rln column
      assertEquals("bigint", columnInfo["rln"]?.get("data_type")?.toString()?.lowercase(), "rln should be of type BIGINT")
      assertEquals("1", columnInfo["rln"]?.get("column_default")?.toString(), "Default value of rln should be 1")

      // Verify rrn column
      assertEquals("bigint", columnInfo["rrn"]?.get("data_type")?.toString()?.lowercase(), "rrn should be of type BIGINT")
      assertEquals("2", columnInfo["rrn"]?.get("column_default")?.toString(), "Default value of rrn should be 2")

      // Verify nlv column
      assertEquals("int", columnInfo["nlv"]?.get("data_type")?.toString()?.lowercase(), "nlv should be of type INT")
      assertEquals("0", columnInfo["nlv"]?.get("column_default")?.toString(), "Default value of nlv should be 0")

      // Verify tgi column
      assertEquals("varchar", columnInfo["tgi"]?.get("data_type")?.toString()?.lowercase(), "tgi should be of type VARCHAR")
      assertEquals("YES", columnInfo["tgi"]?.get("is_nullable"), "tgi should be nullable")
    }

    @Test
    fun `add_presort_tree_struct should create indexes for all columns`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_struct_table(name VARCHAR(255))")

      // Call add_presort_tree_struct
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_struct_table')")

      // Verify indexes are created
      assertTrue(hasIndex("test_presort_tree_struct_table", "rpi_idx"), "Index rpi_idx should be created")
      assertTrue(hasIndex("test_presort_tree_struct_table", "rln_idx"), "Index rln_idx should be created")
      assertTrue(hasIndex("test_presort_tree_struct_table", "rrn_idx"), "Index rrn_idx should be created")
      assertTrue(hasIndex("test_presort_tree_struct_table", "nlv_idx"), "Index nlv_idx should be created")
      assertTrue(hasIndex("test_presort_tree_struct_table", "tgi_idx"), "Index tgi_idx should be created")
    }

    @Test
    fun `add_presort_tree_struct idempotency test`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_presort_tree_struct_table(name VARCHAR(255))")

      // First call
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_struct_table')")
      val afterFirst = getTableColumns("test_presort_tree_struct_table")

      // Second call (idempotency test)
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_struct_table')")
      val afterSecond = getTableColumns("test_presort_tree_struct_table")

      // Third call (further verification)
      jdbcTemplate.execute("CALL add_presort_tree_struct('test_presort_tree_struct_table')")
      val afterThird = getTableColumns("test_presort_tree_struct_table")

      // Verify idempotency
      assertEquals(afterFirst, afterSecond, "Columns should be the same after the second call")
      assertEquals(afterSecond, afterThird, "Columns should be the same after the third call")

      val expectedColumns = listOf("rpi", "rln", "rrn", "nlv", "tgi")
      assertTrue(afterThird.containsAll(expectedColumns), "All presorted tree struct columns should be present")
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
