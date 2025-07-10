package net.yan100.compose.rds.flywaymigrationpostgresql.flyway

import jakarta.annotation.Resource
import kotlin.test.assertTrue
import net.yan100.compose.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@Rollback
class AllToNullableMigrationTest : IDatabasePostgresqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun cleanTables() {
    jdbcTemplate.execute("drop table if exists test_table")
  }

  @Test
  @Transactional
  fun `all_to_nullable 应将所有字段变为可空`() {
    jdbcTemplate.execute("create table test_table(id bigint primary key, name varchar(10) not null)")
    jdbcTemplate.execute("select all_to_nullable('test_table')")
    val nullables =
      jdbcTemplate
        .queryForList(
          """
        select column_name, is_nullable from information_schema.columns
        where table_name = 'test_table'
        and column_name = 'name'
        """
            .trimIndent()
        )
        .associate { it["column_name"] to it["is_nullable"] }
    assertTrue(nullables["name"]?.toString()?.uppercase() == "YES", "字段 name 不是可空，实际: ${nullables["name"]}")
  }

  @Test
  @Transactional
  fun `all_to_nullable 幂等性测试`() {
    jdbcTemplate.execute("create table test_table(id bigint primary key, name varchar(10) not null, age int not null default 18)")
    jdbcTemplate.execute("select all_to_nullable('test_table')")
    jdbcTemplate.execute("select all_to_nullable('test_table')")
    val nullables =
      jdbcTemplate
        .queryForList(
          """
        select column_name, is_nullable, column_default from information_schema.columns
        where table_name = 'test_table' and column_name in ('name', 'age')
        """
            .trimIndent()
        )
        .associate { it["column_name"] to it["is_nullable"] }
    assertTrue("YES" == nullables["name"]?.toString()?.uppercase(), "name 字段不是可空")
    assertTrue("YES" == nullables["age"]?.toString()?.uppercase(), "age 字段不是可空")
  }
}
