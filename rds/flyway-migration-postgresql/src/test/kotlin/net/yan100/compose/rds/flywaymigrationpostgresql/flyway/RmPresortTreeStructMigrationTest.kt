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
class RmPresortTreeStructMigrationTest : IDatabasePostgresqlContainer {
  @Resource
  lateinit var jdbcTemplate: JdbcTemplate

  @Test
  @Transactional
  fun `rm_presort_tree_struct 应正确移除字段`() {
    jdbcTemplate.execute("drop table if exists test_table")
    jdbcTemplate.execute("create table test_table(id bigint primary key)")
    jdbcTemplate.execute("select add_presort_tree_struct('test_table')")
    jdbcTemplate.execute("select rm_presort_tree_struct('test_table')")
    val columns =
      jdbcTemplate
        .queryForList(
          """
        select column_name from information_schema.columns
        where table_name = 'test_table'
        """
            .trimIndent()
        )
        .map { it["column_name"] }
    val removed = listOf("pid", "presort")
    assertTrue(
      removed.none { it in columns },
      "presort tree 字段未被移除: " + (removed.filter { it in columns }),
    )
  }

  @Test
  @Transactional
  fun `rm_presort_tree_struct 幂等性及字段全覆盖测试`() {
    jdbcTemplate.execute("drop table if exists test_table")
    jdbcTemplate.execute("create table test_table(id bigint primary key)")
    jdbcTemplate.execute("select add_presort_tree_struct('test_table')")
    jdbcTemplate.execute("select rm_presort_tree_struct('test_table')")
    jdbcTemplate.execute("select rm_presort_tree_struct('test_table')")
    val columns =
      jdbcTemplate
        .queryForList(
          """
        select column_name from information_schema.columns
        where table_name = 'test_table'
        """
            .trimIndent()
        )
        .map { it["column_name"] }
    val removed = listOf("rln", "rrn", "tgi", "nlv")
    assertTrue(
      removed.none { it in columns },
      "rm_presort_tree_struct 幂等性或字段未被移除: " + (removed.filter { it in columns }),
    )
  }
} 
