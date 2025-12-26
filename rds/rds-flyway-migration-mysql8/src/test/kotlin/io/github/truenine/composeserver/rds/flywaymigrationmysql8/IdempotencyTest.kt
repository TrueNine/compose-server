package io.github.truenine.composeserver.rds.flywaymigrationmysql8

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
 * MySQL stored procedure idempotency test suite.
 *
 * Verifies that all stored procedures can be safely executed multiple times, ensuring that:
 * 1. Repeated calls do not produce errors.
 * 2. Repeated calls do not change the final state.
 * 3. Database object state remains consistent.
 */
@SpringBootTest
@Transactional
@Rollback
class IdempotencyTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    // Clean up potential leftover test tables
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_idempotency_table")
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_index_table")
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_base_struct_table")
  }

  @Nested
  inner class AddBaseStructIdempotencyTests {

    @Test
    fun `add_base_struct should be idempotent on empty table`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255))")

      // Fetch initial table state
      val initialColumns = getTableColumns("test_base_struct_table")
      assertEquals(1, initialColumns.size, "Initial table should have exactly one column")
      assertTrue(initialColumns.contains("name"), "Table should contain column 'name'")

      // First call to add_base_struct
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")
      val afterFirstCall = getTableColumns("test_base_struct_table")

      // Second call to add_base_struct (idempotency check)
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")
      val afterSecondCall = getTableColumns("test_base_struct_table")

      // Third call to add_base_struct (further idempotency verification)
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")
      val afterThirdCall = getTableColumns("test_base_struct_table")

      // Verify all calls produce the same result
      assertEquals(afterFirstCall.size, afterSecondCall.size, "Column count after second call should remain the same")
      assertEquals(afterSecondCall.size, afterThirdCall.size, "Column count after third call should remain the same")
      assertEquals(afterFirstCall.sorted(), afterSecondCall.sorted(), "Column list after second call should remain the same")
      assertEquals(afterSecondCall.sorted(), afterThirdCall.sorted(), "Column list after third call should remain the same")

      // Verify that all base-struct columns exist
      val expectedBaseColumns = listOf("id", "rlv", "crd", "mrd", "ldf", "name")
      assertTrue(afterThirdCall.containsAll(expectedBaseColumns), "All base-struct columns should be present")
      assertEquals(expectedBaseColumns.size, afterThirdCall.size, "Column count should match expected base-struct columns")
    }

    @Test
    fun `add_base_struct should be idempotent when some base columns already exist`() {
      // Create table that already has some base-struct columns
      jdbcTemplate.execute(
        """
        CREATE TABLE test_base_struct_table(
          id BIGINT PRIMARY KEY,
          name VARCHAR(255),
          rlv INT DEFAULT 0
        )
        """
          .trimIndent()
      )

      // Fetch initial table state
      val initialColumns = getTableColumns("test_base_struct_table")
      assertEquals(3, initialColumns.size, "Initial table should have 3 columns")

      // Call add_base_struct multiple times
      repeat(3) { jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')") }

      // Verify final state
      val finalColumns = getTableColumns("test_base_struct_table")
      val expectedColumns = listOf("id", "name", "rlv", "crd", "mrd", "ldf")
      assertTrue(finalColumns.containsAll(expectedColumns), "All base-struct columns should be present")
      assertEquals(expectedColumns.size, finalColumns.size, "Column count should be correct")

      // Verify primary key constraint is still present
      val primaryKeyColumns = getPrimaryKeyColumns("test_base_struct_table")
      assertEquals(listOf("id"), primaryKeyColumns, "Primary key constraint should remain unchanged")
    }

    @Test
    fun `add_base_struct should be idempotent when full base struct already exists`() {
      // Create table that already has full base-struct columns
      jdbcTemplate.execute(
        """
        CREATE TABLE test_base_struct_table(
          id BIGINT PRIMARY KEY AUTO_INCREMENT,
          name VARCHAR(255),
          rlv INT DEFAULT 0,
          crd TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          mrd TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
          ldf TIMESTAMP NULL DEFAULT NULL
        )
        """
          .trimIndent()
      )

      // Capture initial state
      val initialColumns = getTableColumns("test_base_struct_table")
      val initialColumnInfo = getDetailedColumnInfo("test_base_struct_table")

      // Call add_base_struct multiple times
      repeat(3) { jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')") }

      // Verify state remains unchanged
      val finalColumns = getTableColumns("test_base_struct_table")
      val finalColumnInfo = getDetailedColumnInfo("test_base_struct_table")

      assertEquals(initialColumns.sorted(), finalColumns.sorted(), "Column list should remain unchanged")
      assertEquals(initialColumnInfo.size, finalColumnInfo.size, "Column metadata should remain unchanged")
    }
  }

  @Nested
  inner class RmBaseStructIdempotencyTests {

    @Test
    fun `rm_base_struct should be idempotent when base struct exists`() {
      // Create table with full base-struct columns
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")

      // Verify base-struct columns exist
      val initialColumns = getTableColumns("test_base_struct_table")
      assertTrue(initialColumns.contains("id"), "Column 'id' should exist")
      assertTrue(initialColumns.contains("rlv"), "Column 'rlv' should exist")

      // First call to rm_base_struct
      jdbcTemplate.execute("CALL rm_base_struct('test_base_struct_table')")
      val afterFirstCall = getTableColumns("test_base_struct_table")

      // Second call to rm_base_struct (idempotency check)
      jdbcTemplate.execute("CALL rm_base_struct('test_base_struct_table')")
      val afterSecondCall = getTableColumns("test_base_struct_table")

      // Third call to rm_base_struct (further idempotency verification)
      jdbcTemplate.execute("CALL rm_base_struct('test_base_struct_table')")
      val afterThirdCall = getTableColumns("test_base_struct_table")

      // Verify all calls produce the same result
      assertEquals(afterFirstCall.sorted(), afterSecondCall.sorted(), "Column list after second call should remain the same")
      assertEquals(afterSecondCall.sorted(), afterThirdCall.sorted(), "Column list after third call should remain the same")

      // Verify only original column remains
      assertEquals(listOf("name"), afterThirdCall, "Only the original column should remain")

      // Verify primary key constraint has been removed
      val primaryKeyColumns = getPrimaryKeyColumns("test_base_struct_table")
      assertTrue(primaryKeyColumns.isEmpty(), "Primary key constraint should be removed")
    }

    @Test
    fun `rm_base_struct should be idempotent when no base struct exists`() {
      // Create table without base-struct columns
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255), age INT)")

      // Capture initial state
      val initialColumns = getTableColumns("test_base_struct_table")
      assertEquals(2, initialColumns.size, "Initial table should have 2 columns")

      // Call rm_base_struct multiple times (should be safe)
      repeat(3) { jdbcTemplate.execute("CALL rm_base_struct('test_base_struct_table')") }

      // Verify state remains unchanged
      val finalColumns = getTableColumns("test_base_struct_table")
      assertEquals(initialColumns.sorted(), finalColumns.sorted(), "Column list should remain unchanged")
      assertEquals(2, finalColumns.size, "Column count should remain unchanged")
    }
  }

  @Nested
  inner class CtIdxIdempotencyTests {

    @Test
    fun `ct_idx should be idempotent when column exists`() {
      // Create test table
      jdbcTemplate.execute(
        """
        CREATE TABLE test_index_table(
          id BIGINT PRIMARY KEY AUTO_INCREMENT,
          name VARCHAR(255),
          email VARCHAR(255),
          age INT
        )
        """
          .trimIndent()
      )

      // Capture initial index state
      val initialIndexes = getTableIndexes("test_index_table")

      // First call to ct_idx
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'name')")
      val afterFirstCall = getTableIndexes("test_index_table")

      // Second call to ct_idx (idempotency check)
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'name')")
      val afterSecondCall = getTableIndexes("test_index_table")

      // Third call to ct_idx (further idempotency verification)
      jdbcTemplate.execute("CALL ct_idx('test_index_table', 'name')")
      val afterThirdCall = getTableIndexes("test_index_table")

      // Verify index was created successfully
      assertTrue(afterFirstCall.contains("name_idx"), "Index name_idx should be created")

      // Verify idempotency - index count and names remain the same
      assertEquals(afterFirstCall.size, afterSecondCall.size, "Index count after second call should remain the same")
      assertEquals(afterSecondCall.size, afterThirdCall.size, "Index count after third call should remain the same")
      assertEquals(afterFirstCall.sorted(), afterSecondCall.sorted(), "Index list after second call should remain the same")
      assertEquals(afterSecondCall.sorted(), afterThirdCall.sorted(), "Index list after third call should remain the same")

      // Verify there is only one name_idx index
      val nameIndexCount = afterThirdCall.count { it == "name_idx" }
      assertEquals(1, nameIndexCount, "There should be exactly one name_idx index")
    }

    @Test
    fun `ct_idx should be idempotent when creating indexes on multiple columns`() {
      // Create test table
      jdbcTemplate.execute(
        """
        CREATE TABLE test_index_table(
          id BIGINT PRIMARY KEY AUTO_INCREMENT,
          name VARCHAR(255),
          email VARCHAR(255),
          age INT
        )
        """
          .trimIndent()
      )

      // Create indexes for multiple columns, calling each multiple times
      val columns = listOf("name", "email", "age")
      columns.forEach { column -> repeat(3) { jdbcTemplate.execute("CALL ct_idx('test_index_table', '$column')") } }

      // Verify all indexes are created and only once
      val finalIndexes = getTableIndexes("test_index_table")
      columns.forEach { column ->
        val expectedIndexName = "${column}_idx"
        assertTrue(finalIndexes.contains(expectedIndexName), "Index $expectedIndexName should be created")
        val indexCount = finalIndexes.count { it == expectedIndexName }
        assertEquals(1, indexCount, "There should be exactly one $expectedIndexName index")
      }
    }

    @Test
    fun `ct_idx should be idempotent when column does not exist`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_index_table(id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255))")

      // Capture initial index state
      val initialIndexes = getTableIndexes("test_index_table")

      // Attempt to create indexes for a non-existent column multiple times (should be safe)
      repeat(3) { jdbcTemplate.execute("CALL ct_idx('test_index_table', 'nonexistent_column')") }

      // Verify index state remains unchanged
      val finalIndexes = getTableIndexes("test_index_table")
      assertEquals(initialIndexes.sorted(), finalIndexes.sorted(), "Index list should remain unchanged")

      // Verify no index was created for the non-existent column
      val nonexistentIndexes = finalIndexes.filter { it.contains("nonexistent") }
      assertTrue(nonexistentIndexes.isEmpty(), "No indexes should be created for non-existent columns")
    }

    @Test
    fun `ct_idx should be idempotent when index already exists`() {
      // Create test table and manually create index
      jdbcTemplate.execute(
        """
        CREATE TABLE test_index_table(
          id BIGINT PRIMARY KEY AUTO_INCREMENT,
          name VARCHAR(255),
          INDEX name_idx (name)
        )
        """
          .trimIndent()
      )

      // Capture initial index state
      val initialIndexes = getTableIndexes("test_index_table")
      assertTrue(initialIndexes.contains("name_idx"), "Index name_idx should already exist")

      // Call ct_idx multiple times (should detect that index already exists)
      repeat(3) { jdbcTemplate.execute("CALL ct_idx('test_index_table', 'name')") }

      // Verify index state remains unchanged
      val finalIndexes = getTableIndexes("test_index_table")
      assertEquals(initialIndexes.sorted(), finalIndexes.sorted(), "Index list should remain unchanged")

      // Verify there is only one name_idx index
      val nameIndexCount = finalIndexes.count { it == "name_idx" }
      assertEquals(1, nameIndexCount, "There should be exactly one name_idx index")
    }
  }

  @Nested
  inner class CombinedIdempotencyTests {

    @Test
    fun `combined operations should be idempotent`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_idempotency_table(name VARCHAR(255), description TEXT)")

      // Execute a sequence of combined operations, each repeated multiple times
      repeat(3) {
        jdbcTemplate.execute("CALL add_base_struct('test_idempotency_table')")
        jdbcTemplate.execute("CALL ct_idx('test_idempotency_table', 'name')")
        jdbcTemplate.execute("CALL ct_idx('test_idempotency_table', 'rlv')")
        jdbcTemplate.execute("CALL ct_idx('test_idempotency_table', 'crd')")
      }

      // Verify final state
      val finalColumns = getTableColumns("test_idempotency_table")
      val finalIndexes = getTableIndexes("test_idempotency_table")

      // Verify columns are correct
      val expectedColumns = listOf("id", "name", "description", "rlv", "crd", "mrd", "ldf")
      assertTrue(finalColumns.containsAll(expectedColumns), "All expected columns should be present")
      assertEquals(expectedColumns.size, finalColumns.size, "Column count should be correct")

      // Verify indexes are correct
      val expectedIndexes = listOf("name_idx", "rlv_idx", "crd_idx")
      expectedIndexes.forEach { expectedIndex ->
        assertTrue(finalIndexes.contains(expectedIndex), "Index $expectedIndex should be present")
        val indexCount = finalIndexes.count { it == expectedIndex }
        assertEquals(1, indexCount, "There should be exactly one $expectedIndex index")
      }
    }

    @Test
    fun `add and remove operations should be idempotent`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_idempotency_table(name VARCHAR(255))")

      // Add base-struct columns multiple times
      repeat(3) { jdbcTemplate.execute("CALL add_base_struct('test_idempotency_table')") }

      // Verify add operation succeeded
      val afterAdd = getTableColumns("test_idempotency_table")
      assertTrue(afterAdd.contains("id"), "Column 'id' should exist")
      assertTrue(afterAdd.contains("rlv"), "Column 'rlv' should exist")

      // Remove base-struct columns multiple times
      repeat(3) { jdbcTemplate.execute("CALL rm_base_struct('test_idempotency_table')") }

      // Verify remove operation succeeded
      val afterRemove = getTableColumns("test_idempotency_table")
      assertEquals(listOf("name"), afterRemove, "Only the original column should remain")

      // Add base-struct columns again multiple times
      repeat(3) { jdbcTemplate.execute("CALL add_base_struct('test_idempotency_table')") }

      // Verify add operation succeeded again
      val afterSecondAdd = getTableColumns("test_idempotency_table")
      val expectedColumns = listOf("id", "name", "rlv", "crd", "mrd", "ldf")
      assertTrue(afterSecondAdd.containsAll(expectedColumns), "All base-struct columns should be present")
    }
  }

  // Helper method: get all column names of a table
  private fun getTableColumns(tableName: String): List<String> {
    return jdbcTemplate
      .queryForList(
        """
        SELECT column_name
        FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = ?
        ORDER BY ordinal_position
        """
          .trimIndent(),
        String::class.java,
        tableName,
      )
      .filterNotNull()
  }

  // Helper method: get primary key columns of a table
  private fun getPrimaryKeyColumns(tableName: String): List<String> {
    return jdbcTemplate
      .queryForList(
        """
        SELECT kcu.column_name
        FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu ON tc.constraint_name = kcu.constraint_name
          AND tc.table_schema = kcu.table_schema
          AND tc.table_name = kcu.table_name
        WHERE tc.table_schema = DATABASE()
          AND tc.table_name = ?
          AND tc.constraint_type = 'PRIMARY KEY'
        ORDER BY kcu.ordinal_position
        """
          .trimIndent(),
        String::class.java,
        tableName,
      )
      .filterNotNull()
  }

  // Helper method: get all index names of a table
  private fun getTableIndexes(tableName: String): List<String> {
    return jdbcTemplate
      .queryForList(
        """
        SELECT DISTINCT index_name
        FROM information_schema.statistics
        WHERE table_schema = DATABASE()
          AND table_name = ?
          AND index_name != 'PRIMARY'
        ORDER BY index_name
        """
          .trimIndent(),
        String::class.java,
        tableName,
      )
      .filterNotNull()
  }

  // Helper method: get detailed column information of a table
  private fun getDetailedColumnInfo(tableName: String): List<Map<String, Any?>> {
    return jdbcTemplate.queryForList(
      """
      SELECT column_name, data_type, is_nullable, column_default, column_key
      FROM information_schema.columns
      WHERE table_schema = DATABASE()
        AND table_name = ?
      ORDER BY ordinal_position
      """
        .trimIndent(),
      tableName,
    )
  }
}
