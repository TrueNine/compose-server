package io.github.truenine.composeserver.testtoolkit.testcontainers

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

/**
 * # PostgreSQL 数据库测试容器接口
 *
 * 该接口提供了 PostgreSQL 测试容器的标准配置，用于集成测试环境。 通过实现此接口，测试类可以自动获得配置好的 PostgreSQL 测试数据库实例，并可以使用扩展函数进行便捷测试。
 *
 * ## ⚠️ 重要提示：容器重用与数据清理
 *
 * **默认情况下，为了提高测试运行效率，所有容器都是可重用的。** 这意味着容器会在多个测试之间共享，数据库中的数据可能会残留。
 *
 * ### 数据清理责任
 * - **必须在测试中进行数据清理**：使用 `@BeforeEach` 或 `@AfterEach` 清理数据库表
 * - **推荐清理方式**：
 *     - 使用 `@Transactional` + `@Rollback` 进行事务回滚
 *     - 手动执行 `TRUNCATE TABLE` 或 `DELETE FROM` 语句
 *     - 使用 `@Sql` 注解执行清理脚本
 *     - 对于 Flyway 迁移，注意版本号管理避免冲突
 * - **不建议禁用重用**：虽然可以通过配置禁用容器重用，但会显著降低测试性能
 *
 * ### 清理示例
 *
 * ```kotlin
 * @BeforeEach
 * fun cleanupDatabase() {
 *   jdbcTemplate.execute("TRUNCATE TABLE your_table RESTART IDENTITY CASCADE")
 *   // 或者使用 @Sql("classpath:cleanup.sql")
 * }
 * ```
 *
 * ## 特性
 * - 自动配置 PostgreSQL 测试容器
 * - **容器在 Spring 属性注入时自动启动**
 * - 容器重用以提高性能
 * - 提供标准的数据库连接配置
 * - 支持 Spring Test 的动态属性注入
 * - 支持 Flyway/Liquibase 数据库迁移
 * - 支持 PostgreSQL 特有功能（JSON、数组等）
 *
 * ## 使用方式
 *
 * ### 传统方式（向后兼容）
 *
 * ```kotlin
 * @SpringBootTest
 * @Transactional
 * @Rollback
 * class YourTestClass : IDatabasePostgresqlContainer {
 *
 *   @BeforeEach
 *   fun setup() {
 *     // 清理数据库表
 *     jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE")
 *   }
 *
 *   @Test
 *   fun `测试数据库操作`() {
 *     // 你的测试代码
 *   }
 * }
 * ```
 *
 * ### 扩展函数方式（推荐）
 *
 * ```kotlin
 * @SpringBootTest
 * class YourTestClass : IDatabasePostgresqlContainer {
 *
 *   @Test
 *   fun `测试数据库操作`() = postgres(resetToInitialState = true) { container ->
 *     // 数据库会自动重置到初始状态，无需手动清理
 *     // container 是当前的 PostgreSQL 容器实例
 *     jdbcTemplate.execute("INSERT INTO users (name) VALUES ('test')")
 *     // 测试逻辑...
 *   }
 * }
 * ```
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
     * PostgreSQL 测试容器实例
     *
     * 预配置的 PostgreSQL 容器，设置可通过配置自定义：
     * - 数据库名称: 可配置，默认 testdb
     * - 用户名: 可配置，默认 test
     * - 密码: 可配置，默认 test
     * - 版本: 可配置，默认 postgres:17.4-alpine
     * - **容器重用**: 默认启用，多个测试共享同一容器实例
     * - **自动启动**: 容器在 Spring 属性注入时自动启动
     *
     * ⚠️ **重要**: 由于容器重用，数据库数据会在测试间残留，请确保在测试中进行适当的数据清理。 特别注意 Flyway 迁移版本号管理，避免版本冲突。
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
     * 创建并启动 PostgreSQL 容器
     *
     * @return 已启动的 PostgreSQL 容器实例
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
     * PostgreSQL 容器的懒加载实例
     *
     * 用于 containers() 聚合函数，返回已初始化的容器实例。
     *
     * @return 懒加载的 PostgreSQL 容器实例
     */
    @JvmStatic val postgresqlContainerLazy: Lazy<PostgreSQLContainer<*>> by lazy { lazy { container } }

    /**
     * Spring测试环境动态属性配置
     *
     * 自动注入数据库连接相关的配置属性到Spring测试环境中：
     * - JDBC URL
     * - 用户名
     * - 密码
     * - 数据库驱动类名
     *
     * 容器将在此方法调用时自动创建并启动，确保属性值可用。
     *
     * @param registry Spring动态属性注册器
     */
    @JvmStatic
    @DynamicPropertySource
    fun properties(registry: DynamicPropertyRegistry) {
      // 线程安全的容器初始化
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
   * PostgreSQL 容器扩展函数
   *
   * 提供便捷的 PostgreSQL 容器测试方式，支持自动数据重置。 容器已在 Spring 属性注入时启动。
   *
   * @param resetToInitialState 是否重置到初始状态（清空所有用户表），默认为 true
   * @param block 测试执行块，接收当前 PostgreSQL 容器实例作为参数
   * @return 测试执行块的返回值
   */
  fun <T> postgres(resetToInitialState: Boolean = true, block: (PostgreSQLContainer<*>) -> T): T {

    if (resetToInitialState) {
      // 重置 PostgreSQL 到初始状态 - 清空用户创建的表
      try {
        // 获取所有用户创建的表
        val result =
          container.execInContainer(
            "psql",
            "-U",
            container.username,
            "-d",
            container.databaseName,
            "-c",
            """
            DO ${'$'}${'$'} 
            DECLARE 
                r RECORD;
            BEGIN
                -- 删除所有用户表（排除系统表）
                FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public') LOOP
                    EXECUTE 'TRUNCATE TABLE ' || quote_ident(r.tablename) || ' RESTART IDENTITY CASCADE';
                END LOOP;
                -- 删除所有序列（如果有的话）
                FOR r IN (SELECT sequence_name FROM information_schema.sequences WHERE sequence_schema = 'public') LOOP
                    EXECUTE 'ALTER SEQUENCE ' || quote_ident(r.sequence_name) || ' RESTART WITH 1';
                END LOOP;
            END ${'$'}${'$'};
            """
              .trimIndent(),
          )
        if (result.exitCode != 0) {
          org.slf4j.LoggerFactory.getLogger(IDatabasePostgresqlContainer::class.java).warn("重置 PostgreSQL 容器时出现警告: {}", result.stderr)
        }
      } catch (e: Exception) {
        // 如果清理失败，记录警告但继续执行测试
        org.slf4j.LoggerFactory.getLogger(IDatabasePostgresqlContainer::class.java).warn("无法重置 PostgreSQL 容器到初始状态: {}", e.message)
      }
    }

    return block(container)
  }
}
