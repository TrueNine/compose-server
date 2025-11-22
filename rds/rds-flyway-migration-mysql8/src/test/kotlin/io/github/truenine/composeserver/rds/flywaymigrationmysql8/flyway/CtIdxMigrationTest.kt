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
 * ct_idx stored procedure tests.
 *
 * Verifies the behavior and idempotency of the ct_idx stored procedure.
 */
@SpringBootTest
@Transactional
@Rollback
class CtIdxMigrationTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_index_table")
  }

  @Nested
  inner class CtIdxTests {

    @Test
    fun `ct_idx should create index for existing column`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_index_table(name VARCHAR(255), age INT)")

      // Verify there is no index initially
      assertTrue(!hasIndex("test_index_table", "name_idx"), "There should be no name_idx index initially")

      // Call ct_idx to create index for column name
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'name')")

      // Verify index is created successfully
      assertTrue(hasIndex("test_index_table", "name_idx"), "Index name_idx should be created")
    }

    @Test
    fun `ct_idx should create separate indexes for multiple columns`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_index_table(name VARCHAR(255), age INT, email VARCHAR(255))")

      // Create indexes for multiple columns
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'name')")
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'age')")
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'email')")

      // Verify all indexes are created successfully
      assertTrue(hasIndex("test_index_table", "name_idx"), "Index name_idx should be created")
      assertTrue(hasIndex("test_index_table", "age_idx"), "Index age_idx should be created")
      assertTrue(hasIndex("test_index_table", "email_idx"), "Index email_idx should be created")
    }

    @Test
    fun `ct_idx should not create index for non-existent column`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_index_table(name VARCHAR(255))")

      // Get initial index count
      val initialIndexCount = getIndexCount("test_index_table")

      // Attempt to create index for a non-existent column
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'nonexistent_column')")

      // Verify index count does not change
      val afterIndexCount = getIndexCount("test_index_table")
      assertEquals(initialIndexCount, afterIndexCount, "No index should be created for non-existent column")
      assertTrue(!hasIndex("test_index_table", "nonexistent_column_idx"), "Index nonexistent_column_idx should not be created")
    }

    @Test
    fun `ct_idx idempotency test repeated calls should not fail`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_index_table(name VARCHAR(255), age INT)")

      // First call
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'name')")
      val afterFirst = getIndexCount("test_index_table")
      assertTrue(hasIndex("test_index_table", "name_idx"), "Index should be created on first call")

      // Second call (idempotency test)
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'name')")
      val afterSecond = getIndexCount("test_index_table")

      // Third call (further verification)
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'name')")
      val afterThird = getIndexCount("test_index_table")

      // Verify idempotency
      assertEquals(afterFirst, afterSecond, "Index count should be the same after the second call")
      assertEquals(afterSecond, afterThird, "Index count should be the same after the third call")
      assertTrue(hasIndex("test_index_table", "name_idx"), "Index should still exist")
    }

    @Test
    fun `ct_idx should handle index naming convention correctly`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_index_table(user_name VARCHAR(255), user_age INT)")

      // Create indexes for columns with underscores
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'user_name')")
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'user_age')")

      // Verify index names are correct
      assertTrue(hasIndex("test_index_table", "user_name_idx"), "Index user_name_idx should be created")
      assertTrue(hasIndex("test_index_table", "user_age_idx"), "Index user_age_idx should be created")
    }

    @Test
    fun `ct_idx should handle already existing indexes`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_index_table(name VARCHAR(255))")

      // Manually create index
      jdbcTemplate.execute("CREATE INDEX name_idx ON test_index_table(name)")

      // Get index count
      val beforeCount = getIndexCount("test_index_table")
      assertTrue(hasIndex("test_index_table", "name_idx"), "Index name_idx should already exist")

      // Call ct_idx
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'name')")

      // Verify index count does not change
      val afterCount = getIndexCount("test_index_table")
      assertEquals(beforeCount, afterCount, "Index count should not change")
      assertTrue(hasIndex("test_index_table", "name_idx"), "Index should still exist")
    }

    @Test
    fun `ct_idx batch operation test`() {
      // Create test table
      jdbcTemplate.execute(
        """
        CREATE TABLE test_index_table(
          id BIGINT PRIMARY KEY,
          name VARCHAR(255),
          age INT,
          email VARCHAR(255),
          created_at TIMESTAMP
        )
        """
          .trimIndent()
      )

      // Create indexes in batch
      val columns = listOf("name", "age", "email", "created_at")
      columns.forEach { column -> jdbcTemplate.execute("CALL ct_idx('test_index_table', '$column')") }

      // Verify all indexes are created successfully
      columns.forEach { column -> assertTrue(hasIndex("test_index_table", "${column}_idx"), "Index ${column}_idx should be created") }

      // Call again to verify idempotency
      columns.forEach { column -> jdbcTemplate.execute("CALL ct_idx('test_index_table', '$column')") }

      // Verify indexes still exist
      columns.forEach { column -> assertTrue(hasIndex("test_index_table", "${column}_idx"), "Index ${column}_idx should still exist") }
    }
  }

  // Helper methods
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

  private fun getIndexCount(tableName: String): Int {
    return jdbcTemplate.queryForObject(
      """
      SELECT COUNT(DISTINCT index_name) 
      FROM information_schema.statistics 
      WHERE table_schema = DATABASE() AND table_name = ?
      """
        .trimIndent(),
      Int::class.java,
      tableName,
    ) ?: 0
  }
}
