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
 * rm_base_struct stored procedure tests.
 *
 * Verifies the behavior and idempotency of the rm_base_struct stored procedure.
 */
@SpringBootTest
@Transactional
@Rollback
class RmBaseStructMigrationTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanupTables() {
    jdbcTemplate.execute("DROP TABLE IF EXISTS test_base_struct_table")
  }

  @Nested
  inner class RmBaseStructTests {

    @Test
    fun `rm_base_struct should remove all base columns`() {
      // Create test table and add base struct
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")

      // Verify columns exist
      val beforeColumns = getTableColumns("test_base_struct_table")
      assertTrue(beforeColumns.contains("id"), "Column 'id' should exist")
      assertTrue(beforeColumns.contains("rlv"), "Column 'rlv' should exist")

      // Call rm_base_struct
      jdbcTemplate.execute("CALL rm_base_struct('test_base_struct_table')")

      // Verify columns are removed
      val afterColumns = getTableColumns("test_base_struct_table")
      assertTrue(!afterColumns.contains("id"), "Column 'id' should not exist")
      assertTrue(!afterColumns.contains("rlv"), "Column 'rlv' should not exist")
      assertTrue(!afterColumns.contains("crd"), "Column 'crd' should not exist")
      assertTrue(!afterColumns.contains("mrd"), "Column 'mrd' should not exist")
      assertTrue(!afterColumns.contains("ldf"), "Column 'ldf' should not exist")
      assertEquals(1, afterColumns.size, "There should be exactly 1 column")
    }

    @Test
    fun `rm_base_struct idempotency test`() {
      // Create test table and add base struct
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255))")
      jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")

      // First removal
      jdbcTemplate.execute("CALL rm_base_struct('test_base_struct_table')")
      val afterFirst = getTableColumns("test_base_struct_table")

      // Second removal (idempotency test)
      jdbcTemplate.execute("CALL rm_base_struct('test_base_struct_table')")
      val afterSecond = getTableColumns("test_base_struct_table")

      // Third removal (further verification)
      jdbcTemplate.execute("CALL rm_base_struct('test_base_struct_table')")
      val afterThird = getTableColumns("test_base_struct_table")

      // Verify idempotency
      assertEquals(afterFirst, afterSecond, "Columns should be the same after the second call")
      assertEquals(afterSecond, afterThird, "Columns should be the same after the third call")

      // Verify final state
      assertEquals(listOf("name"), afterThird, "Only the original column should remain")
    }

    @Test
    fun `combined operations idempotency test`() {
      // Create test table
      jdbcTemplate.execute("CREATE TABLE test_base_struct_table(name VARCHAR(255))")

      // Repeatedly execute add and remove operations
      repeat(3) {
        jdbcTemplate.execute("CALL add_base_struct('test_base_struct_table')")
        jdbcTemplate.execute("CALL rm_base_struct('test_base_struct_table')")
      }

      // Verify final state
      val finalColumns = getTableColumns("test_base_struct_table")
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
}
