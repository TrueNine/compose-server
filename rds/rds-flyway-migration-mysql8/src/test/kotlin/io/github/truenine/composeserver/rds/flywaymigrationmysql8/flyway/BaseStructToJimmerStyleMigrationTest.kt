package io.github.truenine.composeserver.rds.flywaymigrationmysql8.flyway

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
 * base_struct_to_jimmer_style stored procedure tests.
 *
 * Verifies the behavior and idempotency of the base_struct_to_jimmer_style stored procedure.
 */
@SpringBootTest
@Transactional
@Rollback
class BaseStructToJimmerStyleMigrationTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_jimmer_style_table")
  }

  @Nested
  inner class BaseStructToJimmerStyleTests {

    @Test
    fun `base_struct_to_jimmer_style should adjust rlv column type and defaults correctly`() {
      // Create test table and add base struct
      jdbcTemplate.execute("CREATE TABLE test_jimmer_style_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_base_struct('test_jimmer_style_table')")

      // Call base_struct_to_jimmer_style
      jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_jimmer_style_table')")

      // Verify rlv column properties
      val columnInfo = getColumnInfo("test_jimmer_style_table")
      assertEquals("int", columnInfo["rlv"]?.get("data_type")?.toString()?.lowercase(), "rlv should be of type INT")
      assertEquals("0", columnInfo["rlv"]?.get("column_default")?.toString(), "Default value of rlv should be 0")
      assertEquals("NO", columnInfo["rlv"]?.get("is_nullable"), "rlv should be NOT NULL")
    }

    @Test
    fun `base_struct_to_jimmer_style should convert boolean ldf to timestamp`() {
      // Create test table with boolean-like ldf column
      jdbcTemplate.execute("CREATE TABLE test_jimmer_style_table(name VARCHAR(255), ldf TINYINT(1) DEFAULT 0)")

      // Insert test data
      jdbcTemplate.execute("INSERT INTO test_jimmer_style_table(name, ldf) VALUES('test1', 1)")
      jdbcTemplate.execute("INSERT INTO test_jimmer_style_table(name, ldf) VALUES('test2', 0)")

      // Call base_struct_to_jimmer_style
      jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_jimmer_style_table')")

      // Verify ldf column type conversion
      val columnInfo = getColumnInfo("test_jimmer_style_table")
      assertEquals("timestamp", columnInfo["ldf"]?.get("data_type")?.toString()?.lowercase(), "ldf should be of type TIMESTAMP")
      assertTrue(columnInfo["ldf"]?.get("column_default") == null, "Default value of ldf should be null")
      assertEquals("YES", columnInfo["ldf"]?.get("is_nullable"), "ldf should be nullable")

      // Verify data conversion is correct (true -> timestamp, false/null -> null)
      val results = jdbcTemplate.queryForList("SELECT name, ldf FROM test_jimmer_style_table ORDER BY name")

      val test1Result = results.find { it["name"] == "test1" }
      val test2Result = results.find { it["name"] == "test2" }

      assertTrue(test1Result?.get("ldf") != null, "Rows that were previously true should have a timestamp value")
      assertEquals(null, test2Result?.get("ldf"), "Rows that were previously false should be null")
    }

    @Test
    fun `base_struct_to_jimmer_style should convert int ldf to timestamp and clear data`() {
      // Create test table with int ldf column
      jdbcTemplate.execute("CREATE TABLE test_jimmer_style_table(name VARCHAR(255), ldf INT DEFAULT 0)")

      // Insert test data
      jdbcTemplate.execute("INSERT INTO test_jimmer_style_table(name, ldf) VALUES('test1', 123)")
      jdbcTemplate.execute("INSERT INTO test_jimmer_style_table(name, ldf) VALUES('test2', 456)")

      // Call base_struct_to_jimmer_style
      jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_jimmer_style_table')")

      // Verify ldf column type conversion
      val columnInfo = getColumnInfo("test_jimmer_style_table")
      assertEquals("timestamp", columnInfo["ldf"]?.get("data_type")?.toString()?.lowercase(), "ldf should be of type TIMESTAMP")
      assertTrue(columnInfo["ldf"]?.get("column_default") == null, "Default value of ldf should be null")

      // Verify all data has been cleared to null
      val results = jdbcTemplate.queryForList("SELECT name, ldf FROM test_jimmer_style_table ORDER BY name")
      results.forEach { result -> assertEquals(null, result["ldf"], "All ldf values should be null") }
    }

    @Test
    fun `base_struct_to_jimmer_style should keep timestamp ldf type unchanged`() {
      // Create test table with existing timestamp ldf column
      jdbcTemplate.execute("CREATE TABLE test_jimmer_style_table(name VARCHAR(255), ldf TIMESTAMP DEFAULT CURRENT_TIMESTAMP)")

      // Insert test data
      jdbcTemplate.execute("INSERT INTO test_jimmer_style_table(name) VALUES('test1')")

      // Capture column info before procedure call
      val beforeColumnInfo = getColumnInfo("test_jimmer_style_table")

      // Call base_struct_to_jimmer_style
      jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_jimmer_style_table')")

      // Verify ldf column remains TIMESTAMP but default is set to null
      val afterColumnInfo = getColumnInfo("test_jimmer_style_table")
      assertEquals("timestamp", afterColumnInfo["ldf"]?.get("data_type")?.toString()?.lowercase(), "ldf should remain of type TIMESTAMP")
      assertTrue(afterColumnInfo["ldf"]?.get("column_default") == null, "Default value of ldf should be set to null")
    }

    @Test
    fun `base_struct_to_jimmer_style idempotency test`() {
      // Create test table and add base struct
      jdbcTemplate.execute("CREATE TABLE test_jimmer_style_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_base_struct('test_jimmer_style_table')")

      // First call
      jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_jimmer_style_table')")
      val afterFirst = getColumnInfo("test_jimmer_style_table")

      // Second call (idempotency test)
      jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_jimmer_style_table')")
      val afterSecond = getColumnInfo("test_jimmer_style_table")

      // Third call (further verification)
      jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_jimmer_style_table')")
      val afterThird = getColumnInfo("test_jimmer_style_table")

      // Verify idempotency
      assertEquals(afterFirst["rlv"]?.get("data_type"), afterSecond["rlv"]?.get("data_type"), "rlv type should be the same after the second call")
      assertEquals(afterSecond["rlv"]?.get("data_type"), afterThird["rlv"]?.get("data_type"), "rlv type should be the same after the third call")

      assertEquals(afterFirst["ldf"]?.get("data_type"), afterSecond["ldf"]?.get("data_type"), "ldf type should be the same after the second call")
      assertEquals(afterSecond["ldf"]?.get("data_type"), afterThird["ldf"]?.get("data_type"), "ldf type should be the same after the third call")

      // Verify final state
      assertEquals("int", afterThird["rlv"]?.get("data_type")?.toString()?.lowercase(), "rlv should be of type INT")
      assertEquals("timestamp", afterThird["ldf"]?.get("data_type")?.toString()?.lowercase(), "ldf should be of type TIMESTAMP")
    }

    @Test
    fun `base_struct_to_jimmer_style should handle tables without rlv or ldf`() {
      // Create test table without base columns
      jdbcTemplate.execute("CREATE TABLE test_jimmer_style_table(name VARCHAR(255), age INT)")

      // Calling base_struct_to_jimmer_style should not produce errors
      jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_jimmer_style_table')")

      // Verify that table structure does not change
      val columns = getTableColumns("test_jimmer_style_table")
      assertEquals(listOf("name", "age"), columns, "Table structure should not change")
    }

    @Test
    fun `base_struct_to_jimmer_style end-to-end test`() {
      // Create test table and add base struct
      jdbcTemplate.execute("CREATE TABLE test_jimmer_style_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_base_struct('test_jimmer_style_table')")

      // Manually alter some column types to simulate a non-standard state
      jdbcTemplate.execute("ALTER TABLE test_jimmer_style_table MODIFY COLUMN ldf TINYINT(1) DEFAULT 0")

      // Insert test data
      jdbcTemplate.execute("INSERT INTO test_jimmer_style_table(id, name, ldf) VALUES(1, 'test', 1)")

      // Call base_struct_to_jimmer_style
      jdbcTemplate.execute("CALL base_struct_to_jimmer_style('test_jimmer_style_table')")

      // Verify all columns conform to Jimmer-style conventions
      val columnInfo = getColumnInfo("test_jimmer_style_table")

      // Verify rlv column
      assertEquals("int", columnInfo["rlv"]?.get("data_type")?.toString()?.lowercase(), "rlv should be of type INT")
      assertEquals("0", columnInfo["rlv"]?.get("column_default")?.toString(), "Default value of rlv should be 0")
      assertEquals("NO", columnInfo["rlv"]?.get("is_nullable"), "rlv should be NOT NULL")

      // Verify ldf column
      assertEquals("timestamp", columnInfo["ldf"]?.get("data_type")?.toString()?.lowercase(), "ldf should be of type TIMESTAMP")
      assertTrue(columnInfo["ldf"]?.get("column_default") == null, "Default value of ldf should be null")
      assertEquals("YES", columnInfo["ldf"]?.get("is_nullable"), "ldf should be nullable")

      // Verify data has been converted correctly
      val result = jdbcTemplate.queryForMap("SELECT name, ldf FROM test_jimmer_style_table WHERE name = 'test'")
      assertTrue(result["ldf"] != null, "Rows where ldf was true should have a timestamp value")
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
