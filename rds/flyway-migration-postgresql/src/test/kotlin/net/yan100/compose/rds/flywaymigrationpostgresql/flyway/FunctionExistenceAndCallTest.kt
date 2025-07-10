package net.yan100.compose.rds.flywaymigrationpostgresql.flyway

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import jakarta.annotation.Resource
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@Rollback
class FunctionExistenceAndCallTest : IDatabasePostgresqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanTables() {
    jdbcTemplate.execute("drop table if exists test_table")
  }

  companion object {
    @JvmStatic
    fun functionNamesProvider() =
      listOf(
        "ct_idx",
        "base_struct_to_jimmer_style",
        "add_base_struct",
        "rm_base_struct",
        "all_to_nullable",
        "add_presort_tree_struct",
        "rm_presort_tree_struct",
        "add_tree_struct",
      )
  }

  @ParameterizedTest
  @MethodSource("functionNamesProvider")
  @Transactional
  fun `函数已创建`(functionName: String) {
    val foundFunctions =
      jdbcTemplate
        .queryForList(
          """
      select proname from pg_proc where proname = '$functionName'
      """
            .trimIndent()
        )
        .map { it["proname"] }
    assertTrue(foundFunctions.contains(functionName), "缺少函数: $functionName")
  }

  @Test
  @Transactional
  fun `所有函数都能被调用`() {
    jdbcTemplate.execute("create table if not exists test_table(id bigint primary key)")
    jdbcTemplate.execute("select add_base_struct('test_table')")
    jdbcTemplate.execute("select rm_base_struct('test_table')")
    jdbcTemplate.execute("select all_to_nullable('test_table')")
    jdbcTemplate.execute("select add_presort_tree_struct('test_table')")
    jdbcTemplate.execute("select add_tree_struct('test_table')")
  }
}
