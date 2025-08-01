package io.github.truenine.composeserver.rds.flywaymigrationmysql8

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabaseMysqlContainer
import jakarta.annotation.Resource
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class SimpleMysqlTest : IDatabaseMysqlContainer {
  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @Test
  fun `数据库连接测试`() {
    val result = jdbcTemplate.queryForObject("SELECT 1", Int::class.java)
    assertEquals(1, result, "数据库连接应该正常")
  }

  @Test
  fun `Flyway 迁移表应该存在`() {
    val tableCount =
      jdbcTemplate.queryForObject(
        """
        SELECT COUNT(*) FROM information_schema.tables
        WHERE table_schema = DATABASE() AND table_name = 'flyway_schema_history'
        """
          .trimIndent(),
        Int::class.java,
      )
    assertEquals(1, tableCount, "应该创建 flyway_schema_history 表")
  }

  @Test
  fun `存储过程应该被创建`() {
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
    assertEquals(3, procedureCount, "应该创建 3 个存储过程")
  }

  @Test
  fun `存储过程应该能够调用`() {
    // 创建测试表
    jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS test_proc_table(name VARCHAR(255))")

    // 调用存储过程
    jdbcTemplate.execute("CALL add_base_struct('test_proc_table')")

    // 验证字段是否添加
    val columns =
      jdbcTemplate.queryForList(
        """
            SELECT column_name FROM information_schema.columns
            WHERE table_schema = DATABASE() AND table_name = 'test_proc_table'
            """
          .trimIndent(),
        String::class.java,
      )

    assertEquals(true, columns.contains("id"), "应该添加 id 字段")
    assertEquals(true, columns.contains("rlv"), "应该添加 rlv 字段")

    // 清理
    jdbcTemplate.execute("DROP TABLE test_proc_table")
  }
}
