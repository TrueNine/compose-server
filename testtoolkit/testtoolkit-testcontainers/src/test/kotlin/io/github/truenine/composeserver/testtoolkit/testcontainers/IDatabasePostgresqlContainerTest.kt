package io.github.truenine.composeserver.testtoolkit.testcontainers

import jakarta.annotation.Resource
import java.sql.DriverManager
import java.sql.SQLException
import kotlin.test.*
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
  fun `verify PostgreSQL container starts successfully`() = postgres {
    assertNotNull(it, "PostgreSQL container should exist")
    assertTrue(it.isRunning == true, "PostgreSQL container should be in running state")

    // Verify container works by executing a simple query
    val version = jdbcTemplate.queryForObject("SELECT version()", String::class.java)
    assertNotNull(version, "Should be able to retrieve PostgreSQL version")
    assertTrue(version.contains("PostgreSQL"), "Database should be PostgreSQL")
  }

  @Test
  fun `verify Spring environment contains datasource configuration`() {
    // Verify that required datasource configuration properties exist
    assertNotNull(environment.getProperty("spring.datasource.url"), "Datasource URL should exist")
    assertNotNull(environment.getProperty("spring.datasource.username"), "Datasource username should exist")
    assertNotNull(environment.getProperty("spring.datasource.password"), "Datasource password should exist")
    assertNotNull(environment.getProperty("spring.datasource.driver-class-name"), "Datasource driver class name should exist")

    // Verify URL points to the Testcontainers PostgreSQL instance
    val jdbcUrl = environment.getProperty("spring.datasource.url")
    assertTrue(jdbcUrl?.contains("jdbc:postgresql") == true, "JDBC URL should be a PostgreSQL connection")
  }

  @Test
  fun `verify database connection can be established`() {
    val connection = jdbcTemplate.dataSource?.connection
    assertNotNull(connection, "Should be able to obtain a database connection")

    connection.use { conn ->
      assertTrue(conn.isValid(5), "Database connection should be valid")
      assertEquals("PostgreSQL", conn.metaData.databaseProductName, "Database type should be PostgreSQL")

      // Verify database process state
      val stmt = conn.createStatement()
      val rs = stmt.executeQuery("SELECT pid, state FROM pg_stat_activity WHERE pid = pg_backend_pid()")
      assertTrue(rs.next(), "Should be able to query current connection process state")
      val state = rs.getString("state")
      assertNotNull(state, "Process state should not be null")
    }
  }

  @Test
  fun `verify basic database operations`() {
    val result = jdbcTemplate.queryForObject("SELECT 1", Int::class.java)
    assertEquals(1, result)

    jdbcTemplate.execute("CREATE TEMP TABLE test_table (id int)")
    jdbcTemplate.execute("INSERT INTO test_table (id) VALUES (999)")
    val count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM test_table WHERE id = 999", Int::class.java)
    assertEquals(1, count)
  }

  @Test
  fun `verify container port mapping is correct`() = postgres {
    val mappedPort = it.getMappedPort(5432)
    assertTrue(mappedPort > 0)

    val jdbcUrl = "jdbc:postgresql://localhost:$mappedPort/${it.databaseName}"
    DriverManager.getConnection(jdbcUrl, it.username, it.password).use { conn ->
      assertTrue(conn.isValid(5))
      assertEquals(it.databaseName, conn.catalog)
    }
  }

  @Test
  fun `verify exception thrown for invalid connection`() {
    val invalidJdbcUrl = "jdbc:postgresql://localhost:1234/nonexistent"
    assertFailsWith<SQLException>("Using an invalid connection should throw an exception") { DriverManager.getConnection(invalidJdbcUrl) }
  }

  @Test
  fun `verify database charset configuration`() {
    val charset = jdbcTemplate.queryForObject("SHOW server_encoding", String::class.java)
    assertEquals("UTF8", charset)
  }
}
