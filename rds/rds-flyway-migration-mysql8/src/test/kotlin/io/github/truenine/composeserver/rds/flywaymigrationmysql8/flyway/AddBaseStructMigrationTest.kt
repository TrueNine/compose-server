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
 * add_base_struct stored procedure tests.
 *
 * Verifies the behavior and idempotency of the add_base_struct and rm_base_struct stored procedures.
 */
@SpringBootTest
@Transactional
@Rollback
class AddBaseStructMigrationTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_base_struct_table")
  }

  @Nested
  inner class AddBaseStructTests {

    @Test
    fun `add_base_struct should add all base columns`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255))")

      // Get initial column count
      val initialColumns = getTableColumns("test_base_struct_table")
      assertEquals(1, initialColumns.size, "Initial table should have exactly one column")

      // Call add_base_struct
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")

      // Verify that columns are added
      val afterColumns = getTableColumns("test_base_struct_table")
      val expectedColumns = listOf("id", "name", "rlv", "crd", "mrd", "ldf")
      assertTrue(afterColumns.containsAll(expectedColumns), "All base columns should be present")
      assertEquals(6, afterColumns.size, "There should be 6 columns")
    }

    @Test
    fun `add_base_struct should set correct column types and constraints`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255))")

      // Call add_base_struct
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")

      // Verify column types and constraints
      val columnInfo = getColumnInfo("test_base_struct_table")

      // Verify id column
      assertEquals("bigint", columnInfo["id"]?.get("data_type")?.toString()?.lowercase(), "id should be of type BIGINT")
      assertEquals("NO", columnInfo["id"]?.get("is_nullable"), "id should be NOT NULL")
      assertEquals("PRI", columnInfo["id"]?.get("column_key"), "id should be the primary key")

      // Verify rlv column
      assertEquals("int", columnInfo["rlv"]?.get("data_type")?.toString()?.lowercase(), "rlv should be of type INT")
      assertEquals("NO", columnInfo["rlv"]?.get("is_nullable"), "rlv should be NOT NULL")

      // Verify timestamp columns
      assertEquals("timestamp", columnInfo["crd"]?.get("data_type")?.toString()?.lowercase(), "crd should be of type TIMESTAMP")
      assertEquals("timestamp", columnInfo["mrd"]?.get("data_type")?.toString()?.lowercase(), "mrd should be of type TIMESTAMP")
      assertEquals("timestamp", columnInfo["ldf"]?.get("data_type")?.toString()?.lowercase(), "ldf should be of type TIMESTAMP")
    }

    @Test
    fun `add_base_struct should set correct default values`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255))")

      // Call add_base_struct
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")

      // Insert test data to verify default values (id must be provided because
      // there is no AUTO_INCREMENT)
      jdbcTemplate.execute("INSERT INTO test_base_struct_table(id, name) VALUES(1, 'test')")

      val result = jdbcTemplate.queryForMap("SELECT rlv, crd, mrd, ldf FROM test_base_struct_table WHERE name = 'test'")

      assertEquals(0, result["rlv"], "Default value of rlv should be 0")
      assertTrue(result["crd"] != null, "crd should have a default value (current timestamp)")
      assertEquals(null, result["mrd"], "Default value of mrd should be null")
      assertEquals(null, result["ldf"], "Default value of ldf should be null")
    }

    @Test
    fun `add_base_struct idempotency test`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255))")

      // First call
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")
      val afterFirst = getTableColumns("test_base_struct_table")

      // Second call (idempotency test)
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")
      val afterSecond = getTableColumns("test_base_struct_table")

      // Third call (further verification)
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")
      val afterThird = getTableColumns("test_base_struct_table")

      // Verify idempotency
      assertEquals(afterFirst, afterSecond, "Columns should be the same after the second call")
      assertEquals(afterSecond, afterThird, "Columns should be the same after the third call")

      // Verify final columns
      val expectedColumns = listOf("id", "name", "rlv", "crd", "mrd", "ldf")
      assertTrue(afterThird.containsAll(expectedColumns), "All base columns should be present")
      assertEquals(6, afterThird.size, "There should be 6 columns")
    }

    @Test
    fun `should handle table with existing data`() {
      // Create test table and insert data
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255))")
      jdbcTemplate.execute("INSERT INTO test_base_struct_table(name) VALUES('existing_data')")

      // Call add_base_struct
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")

      // Verify existing data is preserved and default values are set correctly
      val result = jdbcTemplate.queryForMap("SELECT name, rlv, crd, mrd, ldf FROM test_base_struct_table WHERE name = 'existing_data'")

      assertEquals("existing_data", result["name"], "Existing data should remain unchanged")
      assertEquals(0, result["rlv"], "rlv should have the default value 0")
      assertTrue(result["crd"] != null, "crd should have a default value")
      assertEquals(null, result["mrd"], "Default value of mrd should be null")
      assertEquals(null, result["ldf"], "ldf should be null by default")
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
}
