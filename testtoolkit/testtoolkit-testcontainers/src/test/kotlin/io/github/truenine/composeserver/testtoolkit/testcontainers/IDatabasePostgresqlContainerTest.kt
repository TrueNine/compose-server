package io.github.truenine.composeserver.testtoolkit.testcontainers

import jakarta.annotation.Resource
import java.sql.DriverManager
import java.sql.SQLException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class IDatabasePostgresqlContainerTest : IDatabasePostgresqlContainer {
  lateinit var environment: Environment
    @Resource set

  lateinit var jdbcTemplate: JdbcTemplate
    @Resource set

  @Test
  fun `验证 PostgreSQL 容器成功启动`() = postgres {
    assertNotNull(it, "PostgreSQL 容器应该存在")
    assertTrue(it.isRunning == true, "PostgreSQL 容器应该处于运行状态")

    // 通过执行简单查询来验证容器是否正常工作
    val version = jdbcTemplate.queryForObject("SELECT version()", String::class.java)
    assertNotNull(version, "应该能够获取 PostgreSQL 版本信息")
    assertTrue(version.contains("PostgreSQL"), "数据库应该是 PostgreSQL")
  }

  @Test
  fun `验证 Spring 环境中包含数据源配置`() {
    // 验证必要的数据源配置属性是否存在
    assertNotNull(environment.getProperty("spring.datasource.url"), "数据源 URL 应该存在")
    assertNotNull(environment.getProperty("spring.datasource.username"), "数据源用户名应该存在")
    assertNotNull(environment.getProperty("spring.datasource.password"), "数据源密码应该存在")
    assertNotNull(environment.getProperty("spring.datasource.driver-class-name"), "数据源驱动类名应该存在")

    // 验证 URL 是否指向 TestContainers 的 PostgreSQL
    val jdbcUrl = environment.getProperty("spring.datasource.url")
    assertTrue(jdbcUrl?.contains("jdbc:postgresql") == true, "JDBC URL 应该是 PostgreSQL 连接")
  }

  @Test
  fun `验证数据库连接可以成功建立`() {
    val connection = jdbcTemplate.dataSource?.connection
    assertNotNull(connection, "应该能够获取数据库连接")

    connection.use { conn ->
      assertTrue(conn.isValid(5), "数据库连接应该有效")
      assertEquals("PostgreSQL", conn.metaData.databaseProductName, "数据库类型应该是 PostgreSQL")

      // 验证数据库进程状态
      val stmt = conn.createStatement()
      val rs = stmt.executeQuery("SELECT pid, state FROM pg_stat_activity WHERE pid = pg_backend_pid()")
      assertTrue(rs.next(), "应该能够查询到当前连接的进程状态")
      val state = rs.getString("state")
      assertNotNull(state, "进程状态不应为空")
    }
  }

  @Test
  fun `验证数据库基本操作正常`() {
    val result = jdbcTemplate.queryForObject("SELECT 1", Int::class.java)
    assertEquals(1, result)

    jdbcTemplate.execute("CREATE TEMP TABLE test_table (id int)")
    jdbcTemplate.execute("INSERT INTO test_table (id) VALUES (999)")
    val count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_table WHERE id = 999", Int::class.java)
    assertEquals(1, count)
  }

  @Test
  fun `验证容器端口映射正确`() = postgres {
    val mappedPort = it.getMappedPort(5432)
    assertTrue(mappedPort > 0)

    val jdbcUrl = "jdbc:postgresql://localhost:$mappedPort/${it.databaseName}"
    DriverManager.getConnection(jdbcUrl, it.username, it.password).use { conn ->
      assertTrue(conn.isValid(5))
      assertEquals(it.databaseName, conn.catalog)
    }
  }

  @Test
  fun `验证无效连接时抛出异常`() {
    val invalidJdbcUrl = "jdbc:postgresql://localhost:1234/nonexistent"
    assertFailsWith<SQLException>("使用无效连接应该抛出异常") { DriverManager.getConnection(invalidJdbcUrl) }
  }

  @Test
  fun `验证数据库字符集配置`() {
    val charset = jdbcTemplate.queryForObject("SHOW server_encoding", String::class.java)
    assertEquals("UTF8", charset)
  }
}
