package io.github.truenine.composeserver.rds.flywaymigrationmysql8

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabaseMysqlContainer
import jakarta.annotation.Resource
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

/**
 * Simplified MySQL stored procedure idempotency tests.
 *
 * Verifies idempotency of the core stored procedures:
 * 1. add_base_struct can be safely called repeatedly.
 * 2. rm_base_struct can be safely called repeatedly.
 * 3. ct_idx can be safely called repeatedly.
 */
@SpringBootTest
@Transactional
@Rollback
class SimpleIdempotencyTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_simple_table")
  }

  @Test
  fun `add_base_struct idempotency test`() {
    // Create test table
    jdbcTemplate.execute("CREATE TABLE test_simple_table(name VARCHAR(255))")

    // Get initial column count
    val initialCount = getColumnCount("test_simple_table")
    assertEquals(1, initialCount, "Initial table should have exactly one column")

    // First call
    jdbcTemplate.execute("CALL add_base_struct('test_simple_table')")
    val afterFirst = getColumnCount("test_simple_table")

    // Second call (idempotency test)
    jdbcTemplate.execute("CALL add_base_struct('test_simple_table')")
    val afterSecond = getColumnCount("test_simple_table")

    // Third call (further verification)
    jdbcTemplate.execute("CALL add_base_struct('test_simple_table')")
    val afterThird = getColumnCount("test_simple_table")

    // Verify idempotency
    assertEquals(afterFirst, afterSecond, "Column count should be the same after the second call")
    assertEquals(afterSecond, afterThird, "Column count should be the same after the third call")

    // Verify base columns exist
    val columns = getTableColumns("test_simple_table")
    assertTrue(columns.contains("id"), "Column 'id' should exist")
    assertTrue(columns.contains("rlv"), "Column 'rlv' should exist")
    assertTrue(columns.contains("crd"), "Column 'crd' should exist")
    assertTrue(columns.contains("mrd"), "Column 'mrd' should exist")
    assertTrue(columns.contains("ldf"), "Column 'ldf' should exist")
  }

  @Test
  fun `rm_base_struct idempotency test`() {
    // Create test table and add base struct
    jdbcTemplate.execute("CREATE TABLE test_simple_table(name VARCHAR(255))")
    jdbcTemplate.execute("CALL add_base_struct('test_simple_table')")

    // Verify base struct columns exist
    val withBaseStruct = getColumnCount("test_simple_table")
    assertTrue(withBaseStruct > 1, "There should be base-struct columns")

    // First removal
    jdbcTemplate.execute("CALL rm_base_struct('test_simple_table')")
    val afterFirst = getColumnCount("test_simple_table")

    // Second removal (idempotency test)
    jdbcTemplate.execute("CALL rm_base_struct('test_simple_table')")
    val afterSecond = getColumnCount("test_simple_table")

    // Third removal (further verification)
    jdbcTemplate.execute("CALL rm_base_struct('test_simple_table')")
    val afterThird = getColumnCount("test_simple_table")

    // Verify idempotency
    assertEquals(afterFirst, afterSecond, "Column count should be the same after the second call")
    assertEquals(afterSecond, afterThird, "Column count should be the same after the third call")

    // Verify only original column remains
    val columns = getTableColumns("test_simple_table")
    assertEquals(listOf("name"), columns, "Only the original column should remain")
  }

  @Test
  fun `ct_idx idempotency test`() {
    // Create test table
    jdbcTemplate.execute(
      """
      CREATE TABLE test_simple_table(
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        name VARCHAR(255),
        email VARCHAR(255)
      )
      """
        .trimIndent()
    )

    // Get initial index count
    val initialIndexCount = getIndexCount("test_simple_table")

    // First index creation
    jdbcTemplate.execute("CALL ct_idx('test_simple_table', 'name')")
    val afterFirst = getIndexCount("test_simple_table")

    // Second index creation (idempotency test)
    jdbcTemplate.execute("CALL ct_idx('test_simple_table', 'name')")
    val afterSecond = getIndexCount("test_simple_table")

    // Third index creation (further verification)
    jdbcTemplate.execute("CALL ct_idx('test_simple_table', 'name')")
    val afterThird = getIndexCount("test_simple_table")

    // Verify idempotency
    assertEquals(afterFirst, afterSecond, "Index count should be the same after the second call")
    assertEquals(afterSecond, afterThird, "Index count should be the same after the third call")

    // Verify index created successfully
    assertTrue(afterFirst > initialIndexCount, "A new index should have been created")

    // Verify index exists
    val hasNameIndex = hasIndex("test_simple_table", "name_idx")
    assertTrue(hasNameIndex, "Index name_idx should exist")
  }

  @Test
  fun `combined operations idempotency test`() {
    // Create test table
    jdbcTemplate.execute("CREATE TABLE test_simple_table(name VARCHAR(255))")

    // Repeatedly execute combined operations
    repeat(3) {
      jdbcTemplate.execute("CALL add_base_struct('test_simple_table')")
      jdbcTemplate.execute("CALL ct_idx('test_simple_table', 'name')")
      jdbcTemplate.execute("CALL ct_idx('test_simple_table', 'rlv')")
    }

    // Verify final state
    val columns = getTableColumns("test_simple_table")
    val expectedColumns = listOf("id", "name", "rlv", "crd", "mrd", "ldf")
    assertTrue(columns.containsAll(expectedColumns), "All expected columns should be present")

    // Verify indexes
    assertTrue(hasIndex("test_simple_table", "name_idx"), "Index name_idx should exist")
    assertTrue(hasIndex("test_simple_table", "rlv_idx"), "Index rlv_idx should exist")
  }

  // Helper methods
  private fun getColumnCount(tableName: String): Int {
    return jdbcTemplate.queryForObject(
      """
      SELECT COUNT(*) 
      FROM information_schema.columns 
      WHERE table_schema = DATABASE() AND table_name = ?
      """
        .trimIndent(),
      Int::class.java,
      tableName,
    ) ?: 0
  }

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
