package net.yan100.compose.rds.flywaymigrationpostgresql.flyway

import jakarta.annotation.Resource
import net.yan100.compose.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertTrue

@SpringBootTest
@Transactional
@Rollback
class CtIdxMigrationTest : IDatabasePostgresqlContainer {
  @Resource
  lateinit var jdbcTemplate: JdbcTemplate

  @Test
  @Transactional
  fun `ct_idx 应能为字段创建索引`() {
    jdbcTemplate.execute("drop table if exists test_table")
    jdbcTemplate.execute(
      "create table test_table(id bigint primary key, name varchar(10))"
    )
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
