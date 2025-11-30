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
 * add_tree_struct stored procedure tests.
 *
 * Verifies the behavior and idempotency of the add_tree_struct and rm_tree_struct stored procedures.
 */
@SpringBootTest
@Transactional
@Rollback
class AddTreeStructMigrationTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_tree_struct_table")
  }

  @Nested
  inner class AddTreeStructTests {

    @Test
    fun `add_tree_struct should add tree struct column`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_tree_struct_table(name VARCHAR(255))")

      // Call add_tree_struct
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_struct_table')")

      // Verify column is added
      val columns = getTableColumns("test_tree_struct_table")
      assertTrue(columns.contains("rpi"), "Column 'rpi' should exist")

      // Verify column type
      val columnInfo = getColumnInfo("test_tree_struct_table")
      assertEquals("bigint", columnInfo["rpi"]?.get("data_type")?.toString()?.lowercase(), "rpi should be of type BIGINT")
      assertEquals("YES", columnInfo["rpi"]?.get("is_nullable"), "rpi should be nullable")
    }

    @Test
    fun `add_tree_struct should create index`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_tree_struct_table(name VARCHAR(255))")

      // Call add_tree_struct
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_struct_table')")

      // Verify index is created
      assertTrue(hasIndex("test_tree_struct_table", "rpi_idx"), "Index rpi_idx should be created")
    }

    @Test
    fun `add_tree_struct idempotency test`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_tree_struct_table(name VARCHAR(255))")

      // First call
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_struct_table')")
      val afterFirst = getTableColumns("test_tree_struct_table")

      // Second call (idempotency test)
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_struct_table')")
      val afterSecond = getTableColumns("test_tree_struct_table")

      // Third call (further verification)
      jdbcTemplate.execute("CALL add_tree_struct('test_tree_struct_table')")
      val afterThird = getTableColumns("test_tree_struct_table")

      // Verify idempotency
      assertEquals(afterFirst, afterSecond, "Columns should be the same after the second call")
      assertEquals(afterSecond, afterThird, "Columns should be the same after the third call")
      assertTrue(afterThird.contains("rpi"), "Column 'rpi' should exist")
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
