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
  fun `verify MySQL container starts successfully`() = mysql {
    assertNotNull(it, "MySQL container should exist")
    assertTrue(it.isRunning == true, "MySQL container should be in running state")

    // Verify container works by executing a simple query
    val version = jdbcTemplate.queryForObject("SELECT VERSION()", String::class.java)
    assertNotNull(version, "Should be able to retrieve MySQL version")
    assertTrue(version.contains("8.4"), "Database should be MySQL 8.4")
  }

  @Test
  fun `verify Spring environment contains datasource configuration`() {
    // Verify that required datasource configuration properties exist
    assertNotNull(environment.getProperty("spring.datasource.url"), "Datasource URL should exist")
    assertNotNull(environment.getProperty("spring.datasource.username"), "Datasource username should exist")
    assertNotNull(environment.getProperty("spring.datasource.password"), "Datasource password should exist")
    assertNotNull(environment.getProperty("spring.datasource.driver-class-name"), "Datasource driver class name should exist")

    // Verify URL points to the Testcontainers MySQL instance
    val jdbcUrl = environment.getProperty("spring.datasource.url")
    assertTrue(jdbcUrl?.contains("jdbc:mysql") == true, "JDBC URL should be a MySQL connection")
  }

  @Test
  fun `verify database connection can be established`() {
    val connection = jdbcTemplate.dataSource?.connection
    assertNotNull(connection, "Should be able to obtain a database connection")

    connection.use { conn ->
      assertTrue(conn.isValid(5), "Database connection should be valid")
      assertEquals("MySQL", conn.metaData.databaseProductName, "Database type should be MySQL")

      // Verify database connection status
      val stmt = conn.createStatement()
      val rs = stmt.executeQuery("SELECT CONNECTION_ID() as id")
      assertTrue(rs.next(), "Should be able to query current connection id")
      val connectionId = rs.getLong("id")
      assertTrue(connectionId > 0, "Connection id should be greater than 0")
    }
  }

  @Test
  fun `verify basic database operations`() {
    val result = jdbcTemplate.queryForObject("SELECT 1", Int::class.java)
    assertEquals(1, result)

    jdbcTemplate.execute("CREATE TEMPORARY TABLE test_table (id int)")
    jdbcTemplate.execute("INSERT INTO test_table (id) VALUES (999)")
    val count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_table WHERE id = 999", Int::class.java)
    assertEquals(1, count)
  }

  @Test
  fun `verify container port mapping is correct`() = mysql {
    val mappedPort = it.getMappedPort(3306)
    assertTrue(mappedPort > 0)

    val jdbcUrl = "jdbc:mysql://localhost:$mappedPort/${it.databaseName}"
    DriverManager.getConnection(jdbcUrl, it.username, it.password).use { conn ->
      assertTrue(conn.isValid(5))
      assertEquals(it.databaseName, conn.catalog)
    }
  }

  @Test
  fun `verify exception thrown for invalid connection`() {
    val invalidJdbcUrl = "jdbc:mysql://localhost:1234/nonexistent"
    assertFailsWith<SQLException>("Using an invalid connection should throw an exception") { DriverManager.getConnection(invalidJdbcUrl) }
  }

  @Test
  fun `verify database charset configuration`() {
    val charset = jdbcTemplate.queryForObject("SELECT @@character_set_database", String::class.java)
    assertNotNull(charset)
    assertTrue(charset.contains("utf8") || charset == "utf8mb4")
  }

  @Test
  fun `verify MySQL version`() {
    val version = jdbcTemplate.queryForObject("SELECT VERSION()", String::class.java)
    assertNotNull(version)
    assertTrue(version.contains("8.4"))
  }
}
