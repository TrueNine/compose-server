package io.github.truenine.composeserver.rds.flywaymigrationpostgresql.flyway

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import jakarta.annotation.Resource
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.ConnectionCallback
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class CtIdxMigrationTest : IDatabasePostgresqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanTables() {
    jdbcTemplate.execute("drop table if exists test_table")
  }

  @Test
  fun `ct_idx should create index for column`() {
    postgres(resetToInitialState = false) {
      // Use a single connection to perform all operations
      jdbcTemplate.execute(
        ConnectionCallback<Unit> { connection ->
          val statement = connection.createStatement()

          // Create table
          statement.execute("create table test_table(id bigint primary key, name varchar(10))")

          // Call ct_idx function
          statement.execute("select ct_idx('test_table', 'name')")

          // Use pg_stat_user_indexes to check whether the index exists (this is the most reliable way)
          val nameIdxCountResult =
            statement.executeQuery(
              """
          select count(*) 
          from pg_stat_user_indexes 
          where indexrelname = 'name_idx'
        """
            )
          nameIdxCountResult.next()
          val nameIdxCount = nameIdxCountResult.getInt(1)
          val indexExists = nameIdxCount > 0

          assertTrue(indexExists, "name_idx index was not created")
        }
      )
    }
  }
}
