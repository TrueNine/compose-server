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
  fun `ct_idx 应能为字段创建索引`() {
    postgres(resetToInitialState = false) {
      // 使用单个连接执行所有操作
      jdbcTemplate.execute(
        ConnectionCallback<Unit> { connection ->
          val statement = connection.createStatement()

          // 创建表
          statement.execute("create table test_table(id bigint primary key, name varchar(10))")

          // 调用 ct_idx 函数
          statement.execute("select ct_idx('test_table', 'name')")

          // 使用 pg_stat_user_indexes 检查索引是否存在（这是最可靠的方法）
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

          assertTrue(indexExists, "name_idx 索引未创建")
        }
      )
    }
  }
}
