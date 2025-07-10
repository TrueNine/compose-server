package net.yan100.compose.rds.flywaymigrationpostgresql.flyway

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import jakarta.annotation.Resource
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@Rollback
class CtIdxMigrationTest : IDatabasePostgresqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanTables() {
    jdbcTemplate.execute("drop table if exists test_table")
  }

  @Test
  @Transactional
  fun `ct_idx 应能为字段创建索引`() {
    jdbcTemplate.execute("create table test_table(id bigint primary key, name varchar(10))")
    jdbcTemplate.execute("select ct_idx('test_table', 'name')")
    jdbcTemplate.execute("select ct_idx('test_table', 'name')")
    val idx =
      jdbcTemplate.queryForList(
        """
        select indexname from pg_indexes
        where tablename = 'test_table' and indexname = 'name_idx'
        """
          .trimIndent()
      )
    assertTrue(idx.isNotEmpty(), "name_idx 索引未创建")
  }
}
