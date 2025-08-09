package io.github.truenine.composeserver.testtoolkit.testcontainers

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

/**
 * # MySQL 数据库测试容器接口
 *
 * 该接口提供了 MySQL 测试容器的标准配置，用于集成测试环境。 通过实现此接口，测试类可以自动获得配置好的 MySQL 测试数据库实例，并可以使用扩展函数进行便捷测试。
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
 * ### 传统方式（向后兼容）
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
 * ### 扩展函数方式（推荐）
 *
 * ```kotlin
 * @SpringBootTest
 * class YourTestClass : IDatabaseMysqlContainer {
 *
 *   @Test
 *   fun `测试数据库操作`() = mysql(resetToInitialState = true) { container ->
 *     // 数据库会自动重置到初始状态，无需手动清理
 *     // container 是当前的 MySQL 容器实例
 *     jdbcTemplate.execute("INSERT INTO users (name) VALUES ('test')")
 *     // 测试逻辑...
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
interface IDatabaseMysqlContainer : ITestContainerBase {
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
     * - **延迟启动**: 容器在首次使用时才启动
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
        // 移除 start() 调用，容器在使用时才启动
      }
    }

    /**
     * MySQL 容器的懒加载实例
     *
     * 用于 containers() 聚合函数，不会立即创建容器，只有在被调用时才创建。
     *
     * @return 懒加载的 MySQL 容器实例
     */
    @JvmStatic val mysqlContainerLazy: Lazy<MySQLContainer<*>> by lazy { lazy { container } }

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
      // 确保容器已启动（为 @DynamicPropertySource 提供支持）
      if (!container.isRunning) {
        container.start()
      }

      registry.add("spring.datasource.url", container::getJdbcUrl)
      registry.add("spring.datasource.username", container::getUsername)
      registry.add("spring.datasource.password", container::getPassword)
      registry.add("spring.datasource.driver-class-name") { "com.mysql.cj.jdbc.Driver" }
    }
  }

  /**
   * MySQL 容器扩展函数
   *
   * 提供便捷的 MySQL 容器测试方式，支持自动数据重置。 容器将在首次使用时自动启动。
   *
   * @param resetToInitialState 是否重置到初始状态（清空所有用户表），默认为 true
   * @param block 测试执行块，接收当前 MySQL 容器实例作为参数
   * @return 测试执行块的返回值
   */
  fun <T> mysql(resetToInitialState: Boolean = true, block: (MySQLContainer<*>) -> T): T {
    // 确保容器已启动
    if (!container.isRunning) {
      container.start()
    }

    if (resetToInitialState) {
      // 重置 MySQL 到初始状态 - 清空用户创建的表
      try {
        // 获取所有用户创建的表并清空
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
          org.slf4j.LoggerFactory.getLogger(IDatabaseMysqlContainer::class.java).warn("重置 MySQL 容器时出现警告: {}", result.stderr)
        }
      } catch (e: Exception) {
        // 如果清理失败，记录警告但继续执行测试
        org.slf4j.LoggerFactory.getLogger(IDatabaseMysqlContainer::class.java).warn("无法重置 MySQL 容器到初始状态: {}", e.message)
      }
    }

    return block(container)
  }
}
