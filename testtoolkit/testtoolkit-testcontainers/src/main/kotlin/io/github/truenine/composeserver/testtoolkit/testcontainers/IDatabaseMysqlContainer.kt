package io.github.truenine.composeserver.testtoolkit.testcontainers

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

/**
 * MySQL database test container interface.
 *
 * Provides a standard configuration for MySQL test containers used in integration tests. By implementing this interface, test classes can obtain a
 * preconfigured MySQL test database instance and use extension functions for convenient testing.
 *
 * Important: container reuse and data cleanup
 *
 * By default, to improve test performance, all containers are reusable. This means database data may remain between tests.
 *
 * Data cleanup responsibility:
 * - You must clean up database tables in tests (for example using `@BeforeEach` or `@AfterEach`).
 * - Recommended cleanup:
 *     - Use `@Transactional` + `@Rollback` to roll back transactions.
 *     - Manually execute `TRUNCATE TABLE` or `DELETE FROM` statements.
 *     - Use `@Sql` annotations to run cleanup scripts.
 * - It is not recommended to disable reuse because it significantly slows down tests.
 *
 * Features:
 * - Automatically configures a MySQL test container.
 * - Container is started automatically when Spring properties are injected.
 * - Container reuse improves performance.
 * - Provides standard database connection configuration.
 * - Supports Spring Test dynamic property injection.
 * - Supports Flyway/Liquibase database migrations.
 *
 * Usage: see tests implementing this interface directly or use the `mysql` extension function for a more concise style.
 *
 * @see org.testcontainers.junit.jupiter.Testcontainers
 * @see org.testcontainers.containers.MySQLContainer
 * @author TrueNine
 * @since 2025-07-28
 */
@Testcontainers
interface IDatabaseMysqlContainer : ITestContainerBase {
  companion object {
    /**
     * MySQL test container instance.
     *
     * Preconfigured MySQL container, with settings customizable via configuration:
     * - Database name: configurable, default `testdb`.
     * - Username: configurable, default `test`.
     * - Password: configurable, default `test`.
     * - Root password: configurable, default `roottest`.
     * - Image version: configurable, default `mysql:8.0`.
     * - Container reuse is enabled by default so multiple tests share the same instance.
     * - Container is started automatically when Spring properties are injected.
     *
     * Important: because of container reuse, database data will remain between tests, so make sure to perform proper cleanup in tests.
     */
    @Volatile private var _container: MySQLContainer<*>? = null

    @JvmStatic
    val container: MySQLContainer<*>
      get() =
        _container
          ?: throw IllegalStateException(
            "MySQL container not initialized. Make sure your test class is annotated with @SpringBootTest and implements IDatabaseMysqlContainer."
          )

    /**
     * Creates and starts the MySQL container.
     *
     * @return started MySQL container instance
     */
    private fun createAndStartContainer(): MySQLContainer<*> {
      val config = TestcontainersConfigurationHolder.getTestcontainersProperties()
      return MySQLContainer<Nothing>(DockerImageName.parse(config.mysql.image)).apply {
        withReuse(config.reuseAllContainers || config.mysql.reuse)
        withDatabaseName(config.mysql.databaseName)
        withUsername(config.mysql.username)
        withPassword(config.mysql.password)
        withEnv("MYSQL_ROOT_PASSWORD", config.mysql.rootPassword)
        withLabel("reuse.UUID", "mysql-testcontainer-compose-server")
        addExposedPorts(3306)
        start()
      }
    }

    /**
     * Lazily initialized MySQL container instance.
     *
     * Used by containers() aggregation functions to return an initialized container instance.
     *
     * @return lazy MySQL container instance
     */
    @JvmStatic val mysqlContainerLazy: Lazy<MySQLContainer<*>> by lazy { lazy { container } }

    /**
     * Dynamic property configuration for Spring test environments.
     *
     * Automatically injects database connection properties into the Spring test environment:
     * - JDBC URL
     * - username
     * - password
     * - JDBC driver class name
     *
     * The container will be created and started when this method is called, ensuring that property values are available.
     *
     * @param registry Spring dynamic property registry
     */
    @JvmStatic
    @DynamicPropertySource
    fun properties(registry: DynamicPropertyRegistry) {
      // Thread-safe container initialization
      if (_container == null) {
        synchronized(IDatabaseMysqlContainer::class.java) {
          if (_container == null) {
            _container = createAndStartContainer()
          }
        }
      }

      registry.add("spring.datasource.url", container::getJdbcUrl)
      registry.add("spring.datasource.username", container::getUsername)
      registry.add("spring.datasource.password", container::getPassword)
      registry.add("spring.datasource.driver-class-name") { "com.mysql.cj.jdbc.Driver" }
    }
  }

  /**
   * MySQL container extension function.
   *
   * Provides a convenient way to test with a MySQL container and supports automatic data reset. The container has already been started when Spring properties
   * are injected.
   *
   * @param resetToInitialState whether to reset to the initial state (truncate all user tables), default is true
   * @param block test block that receives the current MySQL container instance
   * @return result of the test block
   */
  fun <T> mysql(resetToInitialState: Boolean = true, block: (MySQLContainer<*>) -> T): T {

    if (resetToInitialState) {
      // Reset MySQL to initial state by truncating user-created tables
      try {
        // Retrieve and truncate all user-created tables
        val result =
          container.execInContainer(
            "mysql",
            "-u",
            container.username,
            "-p${container.password}",
            "-D",
            container.databaseName,
            "-e",
            """
          SET FOREIGN_KEY_CHECKS = 0;
          SET @tables = NULL;
          SELECT GROUP_CONCAT(table_name) INTO @tables
            FROM information_schema.tables 
            WHERE table_schema = '${container.databaseName}' 
            AND table_type = 'BASE TABLE';
          
          SET @tables = CONCAT('TRUNCATE TABLE ', @tables);
          PREPARE stmt FROM @tables;
          EXECUTE stmt;
          DEALLOCATE PREPARE stmt;
          SET FOREIGN_KEY_CHECKS = 1;
          """
              .trimIndent(),
          )
        if (result.exitCode != 0) {
          org.slf4j.LoggerFactory.getLogger(IDatabaseMysqlContainer::class.java).warn("Warning while resetting MySQL container: {}", result.stderr)
        }
      } catch (e: Exception) {
        // If cleanup fails, log a warning but continue executing tests
        org.slf4j.LoggerFactory.getLogger(IDatabaseMysqlContainer::class.java).warn("Failed to reset MySQL container to initial state: {}", e.message)
      }
    }

    return block(container)
  }
}
