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
 * all_to_nullable stored procedure tests.
 *
 * Verifies the behavior and idempotency of the all_to_nullable stored procedure.
 */
@SpringBootTest
@Transactional
@Rollback
class AllToNullableMigrationTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_nullable_table")
  }

  @Nested
  inner class AllToNullableTests {

    @Test
    fun `all_to_nullable should set non-primary-key columns nullable`() {
      // Create test table with NOT NULL constraints
      jdbcTemplate.execute(
        """
        CREATE TABLE test_nullable_table(
          id BIGINT PRIMARY KEY,
          name VARCHAR(255) NOT NULL,
          age INT NOT NULL DEFAULT 18,
          email VARCHAR(255) NOT NULL
        )
        """
          .trimIndent()
      )

      // Call all_to_nullable
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")

      // Verify non-primary-key columns become nullable
      val columnInfo = getColumnInfo("test_nullable_table")

      assertEquals("NO", columnInfo["id"]?.get("is_nullable"), "Primary key column should remain NOT NULL")
      assertEquals("YES", columnInfo["name"]?.get("is_nullable"), "Column 'name' should become nullable")
      assertEquals("YES", columnInfo["age"]?.get("is_nullable"), "Column 'age' should become nullable")
      assertEquals("YES", columnInfo["email"]?.get("is_nullable"), "Column 'email' should become nullable")
    }

    @Test
    fun `all_to_nullable should remove default values from non-primary-key columns`() {
      // Create test table with default values
      jdbcTemplate.execute(
        """
        CREATE TABLE test_nullable_table(
          id BIGINT PRIMARY KEY,
          name VARCHAR(255) DEFAULT 'default_name',
          age INT DEFAULT 18,
          status VARCHAR(20) DEFAULT 'active'
        )
        """
          .trimIndent()
      )

      // Call all_to_nullable
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")

      // Verify default values are removed
      val columnInfo = getColumnInfo("test_nullable_table")

      assertTrue(columnInfo["name"]?.get("column_default") == null, "Default value of column 'name' should be removed")
      assertTrue(columnInfo["age"]?.get("column_default") == null, "Default value of column 'age' should be removed")
      assertTrue(columnInfo["status"]?.get("column_default") == null, "Default value of column 'status' should be removed")
    }

    @Test
    fun `all_to_nullable should keep primary key columns unchanged`() {
      // Create test table
      jdbcTemplate.execute(
        """
        CREATE TABLE test_nullable_table(
          id BIGINT PRIMARY KEY,
          user_id BIGINT NOT NULL,
          name VARCHAR(255) NOT NULL
        )
        """
          .trimIndent()
      )

      // Get primary key info before call
      val beforeColumnInfo = getColumnInfo("test_nullable_table")

      // Call all_to_nullable
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")

      // Verify primary key column remains unchanged
      val afterColumnInfo = getColumnInfo("test_nullable_table")

      assertEquals(beforeColumnInfo["id"]?.get("is_nullable"), afterColumnInfo["id"]?.get("is_nullable"), "Nullability of primary key column should not change")
      assertEquals(beforeColumnInfo["id"]?.get("column_key"), afterColumnInfo["id"]?.get("column_key"), "Primary key constraint should not change")
    }

    @Test
    fun `all_to_nullable idempotency test`() {
      // Create test table
      jdbcTemplate.execute(
        """
        CREATE TABLE test_nullable_table(
          id BIGINT PRIMARY KEY,
          name VARCHAR(255) NOT NULL DEFAULT 'test',
          age INT NOT NULL DEFAULT 25
        )
        """
          .trimIndent()
      )

      // First call
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")
      val afterFirst = getColumnInfo("test_nullable_table")

      // Second call (idempotency test)
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")
      val afterSecond = getColumnInfo("test_nullable_table")

      // Third call (further verification)
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")
      val afterThird = getColumnInfo("test_nullable_table")

      // Verify idempotency
      assertEquals(afterFirst["name"]?.get("is_nullable"), afterSecond["name"]?.get("is_nullable"), "Nullability of column 'name' should be the same after the second call")
      assertEquals(afterSecond["name"]?.get("is_nullable"), afterThird["name"]?.get("is_nullable"), "Nullability of column 'name' should be the same after the third call")

      assertEquals(afterFirst["age"]?.get("is_nullable"), afterSecond["age"]?.get("is_nullable"), "Nullability of column 'age' should be the same after the second call")
      assertEquals(afterSecond["age"]?.get("is_nullable"), afterThird["age"]?.get("is_nullable"), "Nullability of column 'age' should be the same after the third call")

      // Verify final state
      assertEquals("YES", afterThird["name"]?.get("is_nullable"), "Column 'name' should be nullable")
      assertEquals("YES", afterThird["age"]?.get("is_nullable"), "Column 'age' should be nullable")
    }

    @Test
    fun `all_to_nullable should handle composite primary keys`() {
      // Create test table with composite primary key
      jdbcTemplate.execute(
        """
        CREATE TABLE test_nullable_table(
          id1 BIGINT NOT NULL,
          id2 BIGINT NOT NULL,
          name VARCHAR(255) NOT NULL,
          age INT NOT NULL,
          PRIMARY KEY (id1, id2)
        )
        """
          .trimIndent()
      )

      // Call all_to_nullable
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")

      // Verify composite primary key columns stay NOT NULL and others become nullable
      val columnInfo = getColumnInfo("test_nullable_table")

      assertEquals("NO", columnInfo["id1"]?.get("is_nullable"), "Composite primary key column 'id1' should remain NOT NULL")
      assertEquals("NO", columnInfo["id2"]?.get("is_nullable"), "Composite primary key column 'id2' should remain NOT NULL")
      assertEquals("YES", columnInfo["name"]?.get("is_nullable"), "Column 'name' should become nullable")
      assertEquals("YES", columnInfo["age"]?.get("is_nullable"), "Column 'age' should become nullable")
    }

    @Test
    fun `all_to_nullable should handle already nullable columns`() {
      // Create test table with already nullable columns
      jdbcTemplate.execute(
        """
        CREATE TABLE test_nullable_table(
          id BIGINT PRIMARY KEY,
          nullable_field VARCHAR(255),
          not_null_field VARCHAR(255) NOT NULL
        )
        """
          .trimIndent()
      )

      // Call all_to_nullable
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")

      // Verify already nullable column stays nullable and NOT NULL column becomes nullable
      val columnInfo = getColumnInfo("test_nullable_table")

      assertEquals("YES", columnInfo["nullable_field"]?.get("is_nullable"), "Already nullable column should remain nullable")
      assertEquals("YES", columnInfo["not_null_field"]?.get("is_nullable"), "Column with NOT NULL constraint should become nullable")
    }
  }

  // Helper methods
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
