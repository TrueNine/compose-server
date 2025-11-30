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
 * V5 all_to_nullable stored procedure tests.
 *
 * Verifies the behavior and idempotency of the all_to_nullable stored procedure.
 */
@SpringBootTest
@Transactional
@Rollback
class V5AllToNullableTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_nullable_table")
  }

  @Nested
  inner class AllToNullableTests {

    @Test
    fun `all_to_nullable should set non-primary-key columns nullable`() {
      // Create test table with primary key and NOT NULL columns
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

      // Verify initial state
      val beforeNullables = getColumnNullability("test_nullable_table")
      assertEquals("NO", beforeNullables["name"], "Column 'name' should be NOT NULL initially")
      assertEquals("NO", beforeNullables["age"], "Column 'age' should be NOT NULL initially")
      assertEquals("NO", beforeNullables["email"], "Column 'email' should be NOT NULL initially")

      // Call all_to_nullable
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")

      // Verify columns become nullable
      val afterNullables = getColumnNullability("test_nullable_table")
      assertEquals("NO", afterNullables["id"], "Primary key column should remain NOT NULL")
      assertEquals("YES", afterNullables["name"], "Column 'name' should become nullable")
      assertEquals("YES", afterNullables["age"], "Column 'age' should become nullable")
      assertEquals("YES", afterNullables["email"], "Column 'email' should become nullable")
    }

    @Test
    fun `all_to_nullable should remove column default values`() {
      // Create test table with default values
      jdbcTemplate.execute(
        """
        CREATE TABLE test_nullable_table(
          id BIGINT PRIMARY KEY,
          name VARCHAR(255) NOT NULL DEFAULT 'default_name',
          age INT NOT NULL DEFAULT 18
        )
        """
          .trimIndent()
      )

      // Verify initial default values
      val beforeDefaults = getColumnDefaults("test_nullable_table")
      assertTrue(beforeDefaults["name"]?.contains("default_name") == true, "Column 'name' should have a default value")
      assertTrue(beforeDefaults["age"]?.contains("18") == true, "Column 'age' should have a default value")

      // Call all_to_nullable
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")

      // Verify default values are removed
      val afterDefaults = getColumnDefaults("test_nullable_table")
      assertEquals(null, afterDefaults["name"], "Default value of column 'name' should be removed")
      assertEquals(null, afterDefaults["age"], "Default value of column 'age' should be removed")
    }

    @Test
    fun `all_to_nullable should preserve primary key constraint`() {
      // Create test table
      jdbcTemplate.execute(
        """
        CREATE TABLE test_nullable_table(
          id BIGINT PRIMARY KEY,
          name VARCHAR(255) NOT NULL
        )
        """
          .trimIndent()
      )

      // Call all_to_nullable
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")

      // Verify primary key constraint remains unchanged
      val nullables = getColumnNullability("test_nullable_table")
      assertEquals("NO", nullables["id"], "Primary key column should remain NOT NULL")

      // Verify primary key constraint still exists
      val primaryKeys = getPrimaryKeyColumns("test_nullable_table")
      assertTrue(primaryKeys.contains("id"), "Column 'id' should still be the primary key")
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
      val afterFirst = getColumnNullability("test_nullable_table")

      // Second call (idempotency test)
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")
      val afterSecond = getColumnNullability("test_nullable_table")

      // Third call (further verification)
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")
      val afterThird = getColumnNullability("test_nullable_table")

      // Verify idempotency
      assertEquals(afterFirst, afterSecond, "Nullability state should be the same after the second call")
      assertEquals(afterSecond, afterThird, "Nullability state should be the same after the third call")

      // Verify final state
      assertEquals("NO", afterThird["id"], "Primary key should remain NOT NULL")
      assertEquals("YES", afterThird["name"], "Column 'name' should be nullable")
      assertEquals("YES", afterThird["age"], "Column 'age' should be nullable")
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
          PRIMARY KEY(id1, id2)
        )
        """
          .trimIndent()
      )

      // Call all_to_nullable
      jdbcTemplate.execute("CALL all_to_nullable('test_nullable_table')")

      // Verify composite primary key columns remain NOT NULL
      val nullables = getColumnNullability("test_nullable_table")
      assertEquals("NO", nullables["id1"], "Composite primary key column 'id1' should remain NOT NULL")
      assertEquals("NO", nullables["id2"], "Composite primary key column 'id2' should remain NOT NULL")
      assertEquals("YES", nullables["name"], "Non-primary-key column 'name' should become nullable")
    }
  }

  // Helper methods
  private fun getColumnNullability(tableName: String): Map<String, String> {
    return jdbcTemplate
      .queryForList(
        """
        SELECT column_name, is_nullable 
        FROM information_schema.columns 
        WHERE table_schema = DATABASE() AND table_name = ?
        """
          .trimIndent(),
        tableName,
      )
      .associate { it["column_name"] as String to it["is_nullable"] as String }
  }

  private fun getColumnDefaults(tableName: String): Map<String, String?> {
    return jdbcTemplate
      .queryForList(
        """
        SELECT column_name, column_default 
        FROM information_schema.columns 
        WHERE table_schema = DATABASE() AND table_name = ?
        """
          .trimIndent(),
        tableName,
      )
      .associate { it["column_name"] as String to it["column_default"] as String? }
  }

  private fun getPrimaryKeyColumns(tableName: String): List<String> {
    return jdbcTemplate
      .queryForList(
        """
        SELECT column_name
        FROM information_schema.key_column_usage
        WHERE table_schema = DATABASE()
          AND table_name = ?
          AND constraint_name = 'PRIMARY'
        """
          .trimIndent(),
        String::class.java,
        tableName,
      )
      .filterNotNull()
  }
}
