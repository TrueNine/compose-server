package net.yan100.compose.rds.flywaymigrationpostgresql

import jakarta.annotation.Resource
import kotlin.test.assertEquals
import net.yan100.compose.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import org.junit.jupiter.api.Assertions.assertTrue
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
class FlywayIntegrationTest : IDatabasePostgresqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

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
    jdbcTemplate.execute("drop table if exists test_table")
    jdbcTemplate.execute(
      "create table if not exists test_table(id bigint primary key)"
    )
    jdbcTemplate.execute("select add_base_struct('test_table')")
    jdbcTemplate.execute("select rm_base_struct('test_table')")
    jdbcTemplate.execute("select all_to_nullable('test_table')")
    jdbcTemplate.execute("select add_presort_tree_struct('test_table')")
    jdbcTemplate.execute("select add_tree_struct('test_table')")
    jdbcTemplate.execute("drop table if exists test_table")
  }

  @Test
  @Transactional
  fun `add_base_struct 应正确增加字段`() {
    // 1. 创建只含 id 的表
    jdbcTemplate.execute("drop table if exists test_table")
    jdbcTemplate.execute("create table test_table(id bigint primary key)")
    // 2. 调用 add_base_struct
    jdbcTemplate.execute("select add_base_struct('test_table')")
    // 3. 查询 test_table 的所有字段
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
    // 4. 断言所有 base_struct 字段都存在
    val expectedColumns = listOf("id", "rlv", "crd", "mrd", "ldf")
    assertTrue(
      columns.containsAll(expectedColumns),
      "缺少字段: " + (expectedColumns - columns),
    )
  }

  @Test
  @Transactional
  fun `rm_base_struct 应正确移除字段`() {
    jdbcTemplate.execute("drop table if exists test_table")
    jdbcTemplate.execute(
      "create table test_table(nick_name varchar default null)"
    )
    jdbcTemplate.execute("select add_base_struct('test_table')")
    jdbcTemplate.execute("select rm_base_struct('test_table')")
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
    // 只剩 id
    assertEquals(
      listOf("nick_name"),
      columns,
      "rm_base_struct 未正确移除字段，当前字段: $columns",
    )
  }

  @Test
  @Transactional
  fun `all_to_nullable 应将所有字段变为可空`() {
    jdbcTemplate.execute("drop table if exists test_table")
    jdbcTemplate.execute(
      "create table test_table(id bigint primary key, name varchar(10) not null)"
    )
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
    assertTrue(
      nullables["name"]?.toString()?.uppercase() == "YES",
      "字段 name 不是可空，实际: ${nullables["name"]}",
    )
  }

  @Test
  @Transactional
  fun `add_presort_tree_struct 应正确增加字段`() {
    jdbcTemplate.execute("drop table if exists test_table")
    jdbcTemplate.execute("create table test_table(id bigint primary key)")
    jdbcTemplate.execute("select add_presort_tree_struct('test_table')")
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
    // SQL 实现实际增加的字段
    val expected = listOf("rpi", "rln", "rrn", "nlv", "tgi")
    assertTrue(
      columns.containsAll(expected),
      "缺少 presort tree 字段: " + (expected - columns),
    )
  }

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
  fun `add_tree_struct 应正确增加字段`() {
    jdbcTemplate.execute("drop table if exists test_table")
    jdbcTemplate.execute("create table test_table(id bigint primary key)")
    jdbcTemplate.execute("select add_tree_struct('test_table')")
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
    // SQL 实现实际增加的字段
    val expected = listOf("rpi")
    assertTrue(
      columns.containsAll(expected),
      "缺少 tree 字段: " + (expected - columns),
    )
  }

  @Test
  @Transactional
  fun `ct_idx 应能为字段创建索引`() {
    jdbcTemplate.execute("drop table if exists test_table")
    jdbcTemplate.execute(
      "create table test_table(id bigint primary key, name varchar(10))"
    )
    jdbcTemplate.execute("select ct_idx('test_table', 'name')")
    // 再次调用应无异常
    jdbcTemplate.execute("select ct_idx('test_table', 'name')")
    // 查询 pg_indexes 验证索引存在
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

  @Test
  @Transactional
  fun `base_struct_to_jimmer_style 应正确修改字段类型和默认值`() {
    jdbcTemplate.execute("drop table if exists test_table")
    jdbcTemplate.execute(
      """
      create table test_table(
        id bigint primary key,
        rlv bigint default 123,
        ldf varchar(32) default '2020-01-01 00:00:00'
      )
    """
        .trimIndent()
    )
    // 调用函数
    jdbcTemplate.execute("select base_struct_to_jimmer_style('test_table')")
    // 检查 rlv 字段类型和默认值
    val rlvInfo =
      jdbcTemplate.queryForMap(
        """
        select data_type, column_default
        from information_schema.columns
        where table_name = 'test_table' and column_name = 'rlv'
      """
          .trimIndent()
      )
    assertEquals("integer", rlvInfo["data_type"], "rlv 字段类型应为 integer")
    assertTrue(
      rlvInfo["column_default"].toString().contains("0"),
      "rlv 字段默认值应为 0",
    )
    // 检查 ldf 字段类型和默认值
    val ldfInfo =
      jdbcTemplate.queryForMap(
        """
        select data_type, column_default
        from information_schema.columns
        where table_name = 'test_table' and column_name = 'ldf'
      """
          .trimIndent()
      )
    assertEquals(
      "timestamp without time zone",
      ldfInfo["data_type"],
      "ldf 字段类型应为 timestamp",
    )
    assertTrue(ldfInfo["column_default"] == null, "ldf 字段默认值应为 null")
  }

  @Test
  @Transactional
  fun `base_struct_to_jimmer_style 应正确处理 boolean ldf 字段`() {
    jdbcTemplate.execute("drop table if exists test_table")
    jdbcTemplate.execute(
      """
      create table test_table(
        id bigint primary key,
        ldf boolean default true
      )
      """
        .trimIndent()
    )
    // 插入多种情况
    jdbcTemplate.execute(
      "insert into test_table(id, ldf) values (1, true), (2, false), (3, null)"
    )
    // 调用函数
    jdbcTemplate.execute("select base_struct_to_jimmer_style('test_table')")
    // 检查 ldf 字段类型
    val ldfInfo =
      jdbcTemplate.queryForMap(
        """
        select data_type, column_default
        from information_schema.columns
        where table_name = 'test_table' and column_name = 'ldf'
        """
          .trimIndent()
      )
    assertEquals(
      "timestamp without time zone",
      ldfInfo["data_type"],
      "ldf 字段类型应为 timestamp",
    )
    assertTrue(ldfInfo["column_default"] == null, "ldf 字段默认值应为 null")
    // 检查数据转换
    val results =
      jdbcTemplate.queryForList("select id, ldf from test_table order by id")
    assertTrue(
      results[0]["ldf"] != null,
      "true 应转为 now()，实际: ${results[0]["ldf"]}",
    )
    assertEquals(null, results[1]["ldf"], "false 应转为 null")
    assertEquals(null, results[2]["ldf"], "null 应转为 null")
  }

  @Test
  @Transactional
  fun `base_struct_to_jimmer_style 幂等性测试`() {
    jdbcTemplate.execute("drop table if exists test_table")
    jdbcTemplate.execute(
      """
        create table test_table(
          id bigint primary key,
          rlv int default 0,
          ldf timestamp default null
        )
      """
        .trimIndent()
    )
    // 连续多次执行
    repeat(3) {
      jdbcTemplate.execute("select base_struct_to_jimmer_style('test_table')")
    }
    val rlvInfo =
      jdbcTemplate.queryForMap(
        """
        select data_type, column_default
        from information_schema.columns
        where table_name = 'test_table' and column_name = 'rlv'
      """
          .trimIndent()
      )
    assertEquals("integer", rlvInfo["data_type"])
    assertTrue(rlvInfo["column_default"].toString().contains("0"))
    val ldfInfo =
      jdbcTemplate.queryForMap(
        """
        select data_type, column_default
        from information_schema.columns
        where table_name = 'test_table' and column_name = 'ldf'
      """
          .trimIndent()
      )
    assertEquals("timestamp without time zone", ldfInfo["data_type"])
    assertTrue(ldfInfo["column_default"] == null)
  }

  @Test
  @Transactional
  fun `base_struct_to_jimmer_style 字段缺失时不报错`() {
    jdbcTemplate.execute("drop table if exists test_table")
    jdbcTemplate.execute("create table test_table(id bigint primary key)")
    jdbcTemplate.execute("select base_struct_to_jimmer_style('test_table')")
    // 能正常执行即通过
  }

  @Test
  @Transactional
  fun `base_struct_to_jimmer_style ldf为int类型时能正确转换`() {
    jdbcTemplate.execute("drop table if exists test_table")
    jdbcTemplate.execute(
      """
        create table test_table(
          id bigint primary key,
          ldf int default 123
        )
      """
        .trimIndent()
    )
    jdbcTemplate.execute("select base_struct_to_jimmer_style('test_table')")
    val ldfInfo =
      jdbcTemplate.queryForMap(
        """
        select data_type, column_default
        from information_schema.columns
        where table_name = 'test_table' and column_name = 'ldf'
      """
          .trimIndent()
      )
    assertEquals("timestamp without time zone", ldfInfo["data_type"])
    assertTrue(ldfInfo["column_default"] == null)
  }

  @Test
  @Transactional
  fun `add_base_struct 应为无 id 且多行数据的表自动填充 id 并设为主键`() {
    jdbcTemplate.execute("drop table if exists test_no_id")
    jdbcTemplate.execute(
      """
      create table test_no_id(nick_name varchar(255), password_enc varchar(255));
      insert into test_no_id(nick_name, password_enc) values ('a', 'p1'), ('b', 'p2'), ('c', 'p3');
      """
        .trimIndent()
    )
    // 调用 add_base_struct
    jdbcTemplate.execute("select add_base_struct('test_no_id')")
    // 检查 id 字段已存在且为主键，且为 0 开始自增
    val columns =
      jdbcTemplate
        .queryForList(
          """
        select column_name from information_schema.columns
        where table_name = 'test_no_id'
      """
            .trimIndent()
        )
        .map { it["column_name"] }
    assertTrue(columns.contains("id"), "未自动添加 id 字段")
    // 检查主键
    val pk =
      jdbcTemplate
        .queryForList(
          """
        select kcu.column_name from information_schema.table_constraints tc
        join information_schema.key_column_usage kcu on tc.constraint_name = kcu.constraint_name
        where tc.table_name = 'test_no_id' and tc.constraint_type = 'PRIMARY KEY'
      """
            .trimIndent()
        )
        .map { it["column_name"] }
    assertTrue(pk.contains("id"), "id 字段未设为主键")
    // 检查 id 值为 0 开始自增
    val ids =
      jdbcTemplate.queryForList("select id from test_no_id order by id").map {
        it["id"]
      }
    assertEquals(listOf(1L, 2L, 3L), ids, "id 字段未按 0 开始自增填充，实际: $ids")
  }
}
