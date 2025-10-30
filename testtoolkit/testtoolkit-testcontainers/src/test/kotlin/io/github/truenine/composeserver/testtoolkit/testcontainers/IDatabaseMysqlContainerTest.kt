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
class IDatabaseMysqlContainerTest : IDatabaseMysqlContainer {
  lateinit var environment: Environment
    @Resource set

  lateinit var jdbcTemplate: JdbcTemplate
    @Resource set

  @Test
  fun `验证 MySQL 容器成功启动`() = mysql {
    assertNotNull(it, "MySQL 容器应该存在")
    assertTrue(it.isRunning == true, "MySQL 容器应该处于运行状态")

    // 通过执行简单查询来验证容器是否正常工作
    val version = jdbcTemplate.queryForObject("SELECT VERSION()", String::class.java)
    assertNotNull(version, "应该能够获取 MySQL 版本信息")
    assertTrue(version.contains("8.4"), "数据库应该是 MySQL 8.4")
  }

  @Test
  fun `验证 Spring 环境中包含数据源配置`() {
    // 验证必要的数据源配置属性是否存在
    assertNotNull(environment.getProperty("spring.datasource.url"), "数据源 URL 应该存在")
    assertNotNull(environment.getProperty("spring.datasource.username"), "数据源用户名应该存在")
    assertNotNull(environment.getProperty("spring.datasource.password"), "数据源密码应该存在")
    assertNotNull(environment.getProperty("spring.datasource.driver-class-name"), "数据源驱动类名应该存在")

    // 验证 URL 是否指向 TestContainers 的 MySQL
    val jdbcUrl = environment.getProperty("spring.datasource.url")
    assertTrue(jdbcUrl?.contains("jdbc:mysql") == true, "JDBC URL 应该是 MySQL 连接")
  }

  @Test
  fun `验证数据库连接可以成功建立`() {
    val connection = jdbcTemplate.dataSource?.connection
    assertNotNull(connection, "应该能够获取数据库连接")

    connection.use { conn ->
      assertTrue(conn.isValid(5), "数据库连接应该有效")
      assertEquals("MySQL", conn.metaData.databaseProductName, "数据库类型应该是 MySQL")

      // 验证数据库连接状态
      val stmt = conn.createStatement()
      val rs = stmt.executeQuery("SELECT CONNECTION_ID() as id")
      assertTrue(rs.next(), "应该能够查询到当前连接的ID")
      val connectionId = rs.getLong("id")
      assertTrue(connectionId > 0, "连接ID应该大于0")
    }
  }

  @Test
  fun `验证数据库基本操作正常`() {
    val result = jdbcTemplate.queryForObject("SELECT 1", Int::class.java)
    assertEquals(1, result)

    jdbcTemplate.execute("CREATE TEMPORARY TABLE test_table (id int)")
    jdbcTemplate.execute("INSERT INTO test_table (id) VALUES (999)")
    val count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_table WHERE id = 999", Int::class.java)
    assertEquals(1, count)
  }

  @Test
  fun `验证容器端口映射正确`() = mysql {
    val mappedPort = it.getMappedPort(3306)
    assertTrue(mappedPort > 0)

    val jdbcUrl = "jdbc:mysql://localhost:$mappedPort/${it.databaseName}"
    DriverManager.getConnection(jdbcUrl, it.username, it.password).use { conn ->
      assertTrue(conn.isValid(5))
      assertEquals(it.databaseName, conn.catalog)
    }
  }

  @Test
  fun `验证无效连接时抛出异常`() {
    val invalidJdbcUrl = "jdbc:mysql://localhost:1234/nonexistent"
    assertFailsWith<SQLException>("使用无效连接应该抛出异常") { DriverManager.getConnection(invalidJdbcUrl) }
  }

  @Test
  fun `验证数据库字符集配置`() {
    val charset = jdbcTemplate.queryForObject("SELECT @@character_set_database", String::class.java)
    assertNotNull(charset)
    assertTrue(charset.contains("utf8") || charset == "utf8mb4")
  }

  @Test
  fun `验证 MySQL 版本`() {
    val version = jdbcTemplate.queryForObject("SELECT VERSION()", String::class.java)
    assertNotNull(version)
    assertTrue(version.contains("8.4"))
  }
}
