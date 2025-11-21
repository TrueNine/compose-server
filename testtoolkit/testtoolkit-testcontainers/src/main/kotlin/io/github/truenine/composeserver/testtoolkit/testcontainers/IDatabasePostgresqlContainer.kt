package io.github.truenine.composeserver.testtoolkit.testcontainers

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

/**
 * PostgreSQL database test container interface.
 *
 * Provides a standard configuration for PostgreSQL test containers used in
 * integration tests. By implementing this interface, test classes can obtain a
 * preconfigured PostgreSQL test database instance and use extension functions
 * for convenient testing.
 *
 * Important: container reuse and data cleanup
 *
 * By default, to improve test performance, all containers are reusable. This
 * means database data may remain between tests.
 *
 * Data cleanup responsibility:
 * - You must clean up database tables in tests (for example using `@BeforeEach`
 *   or `@AfterEach`).
 * - Recommended cleanup:
 *   - Use `@Transactional` + `@Rollback` to roll back transactions.
 *   - Manually execute `TRUNCATE TABLE` or `DELETE FROM` statements.
 *   - Use `@Sql` annotations to run cleanup scripts.
 *   - For Flyway migrations, carefully manage version numbers to avoid
 *     conflicts.
 * - It is not recommended to disable reuse because it significantly slows down
 *   tests.
 *
 * Features:
 * - Automatically configures a PostgreSQL test container.
 * - Container is started automatically when Spring properties are injected.
 * - Container reuse improves performance.
 * - Provides standard database connection configuration.
 * - Supports Spring Test dynamic property injection.
 * - Supports Flyway/Liquibase database migrations.
 * - Supports PostgreSQL-specific features (JSON, arrays, etc.).
 *
 * Usage: see tests implementing this interface directly or use the `postgres`
 * extension function for a more concise style.
 *
 * @see org.testcontainers.junit.jupiter.Testcontainers
 * @see org.testcontainers.containers.PostgreSQLContainer
 * @author TrueNine
 * @since 2025-04-24
 */
@Testcontainers
interface IDatabasePostgresqlContainer : ITestContainerBase {
  companion object {
    /**
     * PostgreSQL test container instance.
     *
     * Preconfigured PostgreSQL container, with settings customizable via
     * configuration:
     * - Database name: configurable, default `testdb`.
     * - Username: configurable, default `test`.
     * - Password: configurable, default `test`.
     * - Image version: configurable, default `postgres:17.4-alpine`.
     * - Container reuse is enabled by default so multiple tests share the same
     *   instance.
     * - Container is started automatically when Spring properties are
     *   injected.
     *
     * Important: because of container reuse, database data will remain between
     * tests, so make sure to perform proper cleanup in tests. Pay special
     * attention to Flyway migration version management to avoid conflicts.
     */
    @Volatile private var _container: PostgreSQLContainer<*>? = null

    @JvmStatic
    val container: PostgreSQLContainer<*>
      get() =
        _container
          ?: throw IllegalStateException(
            "PostgreSQL container not initialized. Make sure your test class is annotated with @SpringBootTest and implements IDatabasePostgresqlContainer."
          )

    /**
     * Creates and starts the PostgreSQL container.
     *
     * @return started PostgreSQL container instance
     */
    private fun createAndStartContainer(): PostgreSQLContainer<*> {
      val config = TestcontainersConfigurationHolder.getTestcontainersProperties()
      return PostgreSQLContainer<Nothing>(DockerImageName.parse(config.postgres.image)).apply {
        withReuse(config.reuseAllContainers || config.postgres.reuse)
        withDatabaseName(config.postgres.databaseName)
        withUsername(config.postgres.username)
        withPassword(config.postgres.password)
        addExposedPorts(5432)
        start()
      }
    }

    /**
     * Lazily initialized PostgreSQL container instance.
     *
     * Used by containers() aggregation functions to return an initialized
     * container instance.
     *
     * @return lazy PostgreSQL container instance
     */
    @JvmStatic val postgresqlContainerLazy: Lazy<PostgreSQLContainer<*>> by lazy { lazy { container } }

    /**
     * Dynamic property configuration for Spring test environments.
     *
     * Automatically injects database connection properties into the Spring
     * test environment:
     * - JDBC URL
     * - username
     * - password
     * - JDBC driver class name
     *
     * The container will be created and started when this method is called,
     * ensuring that property values are available.
     *
     * @param registry Spring dynamic property registry
     */
    @JvmStatic
    @DynamicPropertySource
    fun properties(registry: DynamicPropertyRegistry) {
      // Thread-safe container initialization
      if (_container == null) {
        synchronized(IDatabasePostgresqlContainer::class.java) {
          if (_container == null) {
            _container = createAndStartContainer()
          }
        }
      }

      registry.add("spring.datasource.url", container::getJdbcUrl)
      registry.add("spring.datasource.username", container::getUsername)
      registry.add("spring.datasource.password", container::getPassword)
      registry.add("spring.datasource.driver-class-name") { "org.postgresql.Driver" }
    }
  }

  /**
   * PostgreSQL container extension function.
   *
   * Provides a convenient way to test with a PostgreSQL container and
   * supports automatic data reset. The container has already been started when
   * Spring properties are injected.
   *
   * @param resetToInitialState whether to reset to the initial state (truncate
   *   all user tables), default is true
   * @param block test block that receives the current PostgreSQL container
   *   instance
   * @return result of the test block
   */
  fun <T> postgres(resetToInitialState: Boolean = true, block: (PostgreSQLContainer<*>) -> T): T {

    if (resetToInitialState) {
      // Reset PostgreSQL to initial state by truncating user-created tables
      try {
        // Retrieve all user-created tables
        val result =
          container.execInContainer(
            "psql",
            "-U",
            container.username,
            "-d",
            container.databaseName,
            "-c",
            """
            DO $$ 
            DECLARE 
                r RECORD;
            BEGIN
                -- Delete all user tables (exclude system tables)
                FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public') LOOP
                    EXECUTE 'TRUNCATE TABLE ' || quote_ident(r.tablename) || ' RESTART IDENTITY CASCADE';
                END LOOP;
                -- Reset all sequences (if any)
                FOR r IN (SELECT sequence_name FROM information_schema.sequences WHERE sequence_schema = 'public') LOOP
                    EXECUTE 'ALTER SEQUENCE ' || quote_ident(r.sequence_name) || ' RESTART WITH 1';
                END LOOP;
            END $$;
            """
              .trimIndent(),
          )
        if (result.exitCode != 0) {
          org.slf4j.LoggerFactory.getLogger(IDatabasePostgresqlContainer::class.java).warn("Warning while resetting PostgreSQL container: {}", result.stderr)
        }
      } catch (e: Exception) {
        // If cleanup fails, log a warning but continue executing tests
        org.slf4j.LoggerFactory.getLogger(IDatabasePostgresqlContainer::class.java).warn("Failed to reset PostgreSQL container to initial state: {}", e.message)
      }
    }

    return block(container)
  }
}
