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
  fun `验证 PostgreSQL 容器成功启动`() {
    assertNotNull(postgresqlContainer, "PostgreSQL 容器应该存在")
    assertTrue(postgresqlContainer?.isRunning == true, "PostgreSQL 容器应该处于运行状态")

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
    // 验证可以执行基本的 SQL 操作
    val result = jdbcTemplate.queryForObject("SELECT 1", Int::class.java)
    assertEquals(1, result, "应该能够执行基本的 SQL 查询")

    // 验证可以创建和删除临时表
    jdbcTemplate.execute("CREATE TEMP TABLE test_table (id int)")

    // 验证表结构 - 直接查询表的列定义来验证表结构
    val columnInfo =
      jdbcTemplate.queryForList("SELECT column_name, data_type FROM information_schema.columns WHERE table_name = 'test_table' ORDER BY ordinal_position")
    assertTrue(columnInfo.isNotEmpty(), "临时表应该有列定义")
    assertTrue(columnInfo.any { it["column_name"] == "id" }, "临时表应该包含 id 列")

    // 验证表是否可以正常操作（这是更重要的测试）
    jdbcTemplate.execute("INSERT INTO test_table (id) VALUES (999)")
    val tableOperationTest = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_table WHERE id = 999", Int::class.java)
    assertEquals(1, tableOperationTest, "应该能够向临时表插入数据并查询")

    // 验证表的可操作性 - 添加另一条记录并验证总数
    jdbcTemplate.execute("INSERT INTO test_table (id) VALUES (1)")
    val insertedCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_table", Int::class.java)
    assertEquals(2, insertedCount, "临时表应该包含所有插入的记录")

    // 验证可以删除数据
    jdbcTemplate.execute("DELETE FROM test_table WHERE id = 1")
    val remainingCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_table", Int::class.java)
    assertEquals(1, remainingCount, "删除后应该只剩下一条记录")
  }

  @Test
  fun `验证容器端口映射正确`() {
    val mappedPort = postgresqlContainer?.getMappedPort(5432)
    assertNotNull(mappedPort, "PostgreSQL 端口应该被正确映射")
    assertTrue(mappedPort > 0, "映射端口应该是有效的端口号")

    // 验证端口可访问性
    val databaseName = postgresqlContainer?.databaseName
    assertNotNull(databaseName, "数据库名称不应为空")

    val jdbcUrl = "jdbc:postgresql://localhost:$mappedPort/$databaseName"
    val username = postgresqlContainer?.username
    val password = postgresqlContainer?.password

    assertNotNull(username, "数据库用户名不应为空")
    assertNotNull(password, "数据库密码不应为空")

    DriverManager.getConnection(jdbcUrl, username, password).use { conn ->
      assertTrue(conn.isValid(5), "应该能够通过映射端口建立连接")

      // 验证连接的数据库名称
      assertEquals(databaseName, conn.catalog, "连接的数据库名称应详正确")

      // 验证数据库名称格式
      assertTrue(databaseName.matches(Regex("^[a-zA-Z_][a-zA-Z0-9_]*$")), "数据库名称应符合标准格式")

      // 验证连接属性
      assertTrue(conn.metaData.supportsTransactions(), "应支持事务")
      assertTrue(conn.metaData.supportsStoredProcedures(), "应支持存储过程")
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
    assertEquals("UTF8", charset, "数据库字符集应该是 UTF8")

    // 验证客户端编码
    val clientEncoding = jdbcTemplate.queryForObject("SHOW client_encoding", String::class.java)
    assertEquals("UTF8", clientEncoding, "客户端字符集应该是 UTF8")
  }

  @Test
  fun `验证数据库时区配置`() {
    val timezone = jdbcTemplate.queryForObject("SHOW timezone", String::class.java)
    assertNotNull(timezone, "数据库时区设置应该存在")

    // 验证时区格式和有效性
    assertTrue(timezone.isNotEmpty(), "时区设置不应为空")

    // 验证时区格式（容错处理，因为不同环境可能有不同设置）
    assertTrue(timezone.matches(Regex("^[A-Za-z_/+-]+$")) || timezone == "UTC" || timezone.contains("/"), "时区格式应符合标准 (actual: $timezone)")

    // 验证时区设置可用性
    val currentTime = jdbcTemplate.queryForObject("SELECT NOW()", java.sql.Timestamp::class.java)
    assertNotNull(currentTime, "应详能获取当前时间")

    // 验证时间的合理性（在过去1分钟到未来1分钟之间）
    val now = System.currentTimeMillis()
    val timeDiff = kotlin.math.abs(currentTime.time - now)
    assertTrue(timeDiff < 60000, "数据库时间应与系统时间接近 (差值: ${timeDiff}ms)")
  }
}
