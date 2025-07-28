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
  fun `验证 MySQL 容器成功启动`() {
    assertNotNull(mysqlContainer, "MySQL 容器应该存在")
    assertTrue(mysqlContainer?.isRunning == true, "MySQL 容器应该处于运行状态")

    // 通过执行简单查询来验证容器是否正常工作
    val version = jdbcTemplate.queryForObject("SELECT VERSION()", String::class.java)
    assertNotNull(version, "应该能够获取 MySQL 版本信息")
    assertTrue(version.contains("8.0"), "数据库应该是 MySQL 8.0")
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
    // 验证可以执行基本的 SQL 操作
    val result = jdbcTemplate.queryForObject("SELECT 1", Int::class.java)
    assertEquals(1, result, "应该能够执行基本的 SQL 查询")

    // 验证可以创建和删除临时表
    jdbcTemplate.execute("CREATE TEMPORARY TABLE test_table (id int)")

    // 验证表结构 - 对于临时表，直接通过 DESCRIBE 命令验证
    val columnInfo = jdbcTemplate.queryForList("DESCRIBE test_table")
    assertTrue(columnInfo.isNotEmpty(), "临时表应该有列定义")
    assertTrue(columnInfo.any { it["Field"] == "id" }, "临时表应该包含 id 列")

    // 验证表是否可以正常操作
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
    val mappedPort = mysqlContainer?.getMappedPort(3306)
    assertNotNull(mappedPort, "MySQL 端口应该被正确映射")
    assertTrue(mappedPort > 0, "映射端口应该是有效的端口号")

    // 验证端口可访问性
    val databaseName = mysqlContainer?.databaseName
    assertNotNull(databaseName, "数据库名称不应为空")

    val jdbcUrl = "jdbc:mysql://localhost:$mappedPort/$databaseName"
    val username = mysqlContainer?.username
    val password = mysqlContainer?.password

    assertNotNull(username, "数据库用户名不应为空")
    assertNotNull(password, "数据库密码不应为空")

    DriverManager.getConnection(jdbcUrl, username, password).use { conn ->
      assertTrue(conn.isValid(5), "应该能够通过映射端口建立连接")

      // 验证连接的数据库名称
      assertEquals(databaseName, conn.catalog, "连接的数据库名称应该正确")

      // 验证数据库名称格式
      assertTrue(databaseName.matches(Regex("^[a-zA-Z_][a-zA-Z0-9_]*$")), "数据库名称应符合标准格式")

      // 验证连接属性
      assertTrue(conn.metaData.supportsTransactions(), "应支持事务")
      assertTrue(conn.metaData.supportsStoredProcedures(), "应支持存储过程")
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
    assertNotNull(charset, "数据库字符集应该存在")

    // MySQL 8.0 默认字符集是 utf8mb4
    assertTrue(charset.contains("utf8") || charset == "utf8mb4", "数据库字符集应该是 UTF8 相关 (actual: $charset)")

    // 验证客户端连接编码
    val connectionCharset = jdbcTemplate.queryForObject("SELECT @@character_set_connection", String::class.java)
    assertNotNull(connectionCharset, "连接字符集应该存在")
  }

  @Test
  fun `验证数据库时区配置`() {
    val timezone = jdbcTemplate.queryForObject("SELECT @@system_time_zone", String::class.java)
    assertNotNull(timezone, "数据库时区设置应该存在")

    // 验证时区设置不为空
    assertTrue(timezone.isNotEmpty(), "时区设置不应为空")

    // 验证可以获取当前时间
    val currentTime = jdbcTemplate.queryForObject("SELECT NOW()", java.sql.Timestamp::class.java)
    assertNotNull(currentTime, "应该能获取当前时间")

    // 验证时间的合理性 - 考虑时区差异，允许更大的时间差
    val now = System.currentTimeMillis()
    val timeDiff = kotlin.math.abs(currentTime.time - now)
    // 允许最大24小时的时区差异加上1分钟的执行时间差
    val maxAllowedDiff = 24 * 60 * 60 * 1000 + 60000 // 24小时 + 1分钟
    assertTrue(timeDiff < maxAllowedDiff, "数据库时间应在合理范围内 (差值: ${timeDiff}ms, 约${timeDiff / 3600000}小时)")
  }

  @Test
  fun `验证 MySQL 特性支持`() {
    // 验证 MySQL 版本特性
    val version = jdbcTemplate.queryForObject("SELECT VERSION()", String::class.java)
    assertNotNull(version, "MySQL 版本信息不应为空")
    assertTrue(version!!.startsWith("8.0"), "应该是 MySQL 8.0 版本")

    // 验证存储引擎支持
    val engines = jdbcTemplate.queryForList("SHOW ENGINES")
    assertTrue(engines.any { (it["Engine"] as String).contains("InnoDB") }, "应该支持 InnoDB 存储引擎")

    // 验证 SQL 模式
    val sqlMode = jdbcTemplate.queryForObject("SELECT @@sql_mode", String::class.java)
    assertNotNull(sqlMode, "SQL 模式应该存在")
  }
}
