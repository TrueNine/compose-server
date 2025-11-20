package io.github.truenine.composeserver.rds.flywaymigrationpostgresql.flyway

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import jakarta.annotation.Resource
import kotlin.test.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@Rollback
class BaseStructToJimmerStyleMigrationTest : IDatabasePostgresqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanTables() {
    jdbcTemplate.execute("drop table if exists test_table")
  }

  @Test
  @Transactional
  fun `base_struct_to_jimmer_style 应正确转换 rlv 字段类型及 default`() {
    jdbcTemplate.execute("create table test_table(id bigint primary key, rlv varchar(10))")
    jdbcTemplate.execute("select add_base_struct('test_table')")
    jdbcTemplate.execute("alter table test_table alter column rlv type varchar(10)")
    jdbcTemplate.execute("alter table test_table alter column rlv set default '1'")
    jdbcTemplate.execute("select base_struct_to_jimmer_style('test_table')")
    val rlvInfo =
      jdbcTemplate.queryForMap(
        """
        select data_type, column_default from information_schema.columns
        where table_name = 'test_table' and column_name = 'rlv'
        """
          .trimIndent()
      )
    assertEquals("integer", rlvInfo["data_type"])
    assertEquals("0", rlvInfo["column_default"].toString().replace("::integer", ""))
  }

  @Test
  @Transactional
  fun `base_struct_to_jimmer_style 应正确转换 ldf 字段类型及 default`() {
    jdbcTemplate.execute("create table test_table(id bigint primary key, ldf boolean default true)")
    jdbcTemplate.execute("select add_base_struct('test_table')")
    jdbcTemplate.execute("alter table test_table alter column ldf type boolean")
    jdbcTemplate.execute("alter table test_table alter column ldf set default true")
    jdbcTemplate.execute("select base_struct_to_jimmer_style('test_table')")
    val ldfInfo =
      jdbcTemplate.queryForMap(
        """
        select data_type, column_default from information_schema.columns
        where table_name = 'test_table' and column_name = 'ldf'
        """
          .trimIndent()
      )
    assertEquals("timestamp without time zone", ldfInfo["data_type"])
    assertEquals(null, ldfInfo["column_default"])
  }

  @Test
  @Transactional
  fun `base_struct_to_jimmer_style 幂等性测试`() {
    jdbcTemplate.execute("create table test_table(id bigint primary key, rlv varchar(10), ldf boolean)")
    jdbcTemplate.execute("select add_base_struct('test_table')")
    jdbcTemplate.execute("select base_struct_to_jimmer_style('test_table')")
    jdbcTemplate.execute("select base_struct_to_jimmer_style('test_table')")
    val rlvInfo =
      jdbcTemplate.queryForMap(
        """
        select data_type, column_default from information_schema.columns
        where table_name = 'test_table' and column_name = 'rlv'
        """
          .trimIndent()
      )
    val ldfInfo =
      jdbcTemplate.queryForMap(
        """
        select data_type, column_default from information_schema.columns
        where table_name = 'test_table' and column_name = 'ldf'
        """
          .trimIndent()
      )
    assertEquals("integer", rlvInfo["data_type"])
    assertEquals("0", rlvInfo["column_default"].toString().replace("::integer", ""))
    assertEquals("timestamp without time zone", ldfInfo["data_type"])
  }
}
