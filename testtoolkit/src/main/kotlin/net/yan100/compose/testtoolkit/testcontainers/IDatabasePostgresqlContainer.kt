package net.yan100.compose.testtoolkit.testcontainers

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * # 数据库测试容器接口
 *
 * 该接口提供了PostgreSQL测试容器的标准配置，用于集成测试环境。
 * 通过实现此接口，测试类可以自动获得配置好的PostgreSQL测试数据库实例。
 *
 * ## 特性
 * - 自动配置PostgreSQL测试容器
 * - 提供标准的数据库连接配置
 * - 支持Spring Test的动态属性注入
 *
 * ## 使用方式
 * ```kotlin
 * @SpringBootTest
 * class YourTestClass : IDatabasePostgresqlContainer {
 *   // 你的测试代码
 * }
 * ```
 * @author TrueNine
 * @since 2025-04-24
 * @see org.testcontainers.junit.jupiter.Testcontainers
 * @see org.testcontainers.containers.PostgreSQLContainer
 */
@Testcontainers
interface IDatabasePostgresqlContainer {
  companion object {
    /**
     * PostgreSQL 测试容器实例
     *
     * 预配置的 PostgreSQL 容器，具有以下默认设置：
     * - 数据库名称: testdb
     * - 用户名: test
     * - 密码: test
     */
    @JvmStatic
    val container by lazy {
      PostgreSQLContainer<Nothing>("postgres:17.4-alpine").apply {
        withDatabaseName("testdb")
        withUsername("test")
        withPassword("test")
        addExposedPorts(5432)
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
      registry.add("spring.datasource.driver-class-name") { "org.postgresql.Driver" }
    }
  }

  val postgresqlContainer: PostgreSQLContainer<*>? get() = container
}
