package io.github.truenine.composeserver.testtoolkit.testcontainers

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

/**
 * # MySQL 数据库测试容器接口
 *
 * 该接口提供了 MySQL 测试容器的标准配置，用于集成测试环境。 通过实现此接口，测试类可以自动获得配置好的 MySQL 测试数据库实例。
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
 * - **不建议禁用重用**：虽然可以通过配置禁用容器重用，但会显著降低测试性能
 *
 * ### 清理示例
 *
 * ```kotlin
 * @BeforeEach
 * fun cleanupDatabase() {
 *   jdbcTemplate.execute("TRUNCATE TABLE your_table")
 *   // 或者使用 @Sql("classpath:cleanup.sql")
 * }
 * ```
 *
 * ## 特性
 * - 自动配置 MySQL 测试容器
 * - 容器重用以提高性能
 * - 提供标准的数据库连接配置
 * - 支持 Spring Test 的动态属性注入
 * - 支持 Flyway/Liquibase 数据库迁移
 *
 * ## 使用方式
 *
 * ```kotlin
 * @SpringBootTest
 * @Transactional
 * @Rollback
 * class YourTestClass : IDatabaseMysqlContainer {
 *
 *   @BeforeEach
 *   fun setup() {
 *     // 清理数据库表
 *     jdbcTemplate.execute("TRUNCATE TABLE users")
 *   }
 *
 *   @Test
 *   fun `测试数据库操作`() {
 *     // 你的测试代码
 *   }
 * }
 * ```
 *
 * @see org.testcontainers.junit.jupiter.Testcontainers
 * @see org.testcontainers.containers.MySQLContainer
 * @author TrueNine
 * @since 2025-07-28
 */
@Testcontainers
interface IDatabaseMysqlContainer {
  companion object {
    /**
     * MySQL 测试容器实例
     *
     * 预配置的 MySQL 容器，设置可通过配置自定义：
     * - 数据库名称: 可配置，默认 testdb
     * - 用户名: 可配置，默认 test
     * - 密码: 可配置，默认 test
     * - 根密码: 可配置，默认 roottest
     * - 版本: 可配置，默认 mysql:8.0
     * - **容器重用**: 默认启用，多个测试共享同一容器实例
     *
     * ⚠️ **重要**: 由于容器重用，数据库数据会在测试间残留，请确保在测试中进行适当的数据清理。
     */
    @JvmStatic
    val container by lazy {
      val config = TestcontainersConfigurationHolder.getTestcontainersProperties()
      MySQLContainer<Nothing>(DockerImageName.parse(config.mysql.image)).apply {
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
     * Spring测试环境动态属性配置
     *
     * 自动注入数据库连接相关的配置属性到Spring测试环境中：
     * - JDBC URL
     * - 用户名
     * - 密码
     * - 数据库驱动类名
     *
     * @param registry Spring动态属性注册器
     */
    @JvmStatic
    @DynamicPropertySource
    fun properties(registry: DynamicPropertyRegistry) {
      registry.add("spring.datasource.url", container::getJdbcUrl)
      registry.add("spring.datasource.username", container::getUsername)
      registry.add("spring.datasource.password", container::getPassword)
      registry.add("spring.datasource.driver-class-name") { "com.mysql.cj.jdbc.Driver" }
    }
  }

  val mysqlContainer: MySQLContainer<*>?
    get() = container
}
