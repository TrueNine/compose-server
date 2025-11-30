package io.github.truenine.composeserver.rds.flywaymigrationmysql8

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabaseMysqlContainer
import jakarta.annotation.Resource
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import kotlin.test.assertEquals

@SpringBootTest
class SimpleMysqlTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @Test
  fun `database connection test`() {
    val result = jdbcTemplate.queryForObject("SELECT 1", Int::class.java)
    assertEquals(1, result, "Database connection should work")
  }

  @Test
  fun `Flyway migration table should exist`() {
    val tableCount =
      jdbcTemplate.queryForObject(
        """
        SELECT COUNT(*) FROM information_schema.tables
        WHERE table_schema = DATABASE() AND table_name = 'flyway_schema_history'
        """
          .trimIndent(),
        Int::class.java,
      )
    assertEquals(1, tableCount, "Table flyway_schema_history should be created")
  }

  @Test
  fun `stored procedures should be created`() {
    val procedureCount =
      jdbcTemplate.queryForObject(
        """
        SELECT COUNT(*) FROM information_schema.routines
        WHERE routine_schema = DATABASE()
          AND routine_name IN ('ct_idx', 'add_base_struct', 'rm_base_struct')
          AND routine_type = 'PROCEDURE'
        """
          .trimIndent(),
        Int::class.java,
      )
    assertEquals(3, procedureCount, "3 stored procedures should be created")
  }

  @Test
  fun `stored procedures should be callable`() {
    // Create test table
    jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS test_proc_table(name VARCHAR(255))")

    // Call stored procedure
    jdbcTemplate.execute("CALL add_base_struct('test_proc_table')")

    // Verify columns are added
    val columns =
      jdbcTemplate.queryForList(
        """
        SELECT column_name FROM information_schema.columns
        WHERE table_schema = DATABASE() AND table_name = 'test_proc_table'
        """
          .trimIndent(),
        String::class.java,
      )

    assertEquals(true, columns.contains("id"), "Column 'id' should be added")
    assertEquals(true, columns.contains("rlv"), "Column 'rlv' should be added")

    // Clean up
    jdbcTemplate.execute("DROP TABLE test_proc_table")
  }
}
