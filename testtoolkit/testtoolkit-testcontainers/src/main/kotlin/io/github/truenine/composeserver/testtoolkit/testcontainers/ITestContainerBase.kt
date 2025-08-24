package io.github.truenine.composeserver.testtoolkit.testcontainers

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.PostgreSQLContainer

/**
 * # 测试容器基础接口
 *
 * 所有测试容器接口的基础接口，提供容器聚合功能。 测试类必须实现此接口或其子接口才能使用 `containers()` 聚合函数。
 *
 * ## 功能特性
 * - 提供多容器聚合能力
 * - 统一容器生命周期管理
 * - 支持容器间协调测试
 * - **容器在 Spring 属性注入时自动启动**
 *
 * ## 使用方式
 *
 * ```kotlin
 * @SpringBootTest
 * class MyTest : ICacheRedisContainer, IDatabasePostgresqlContainer {
 *   @Test
 *   fun `多容器测试`() = containers(
 *     redisContainerLazy,
 *     postgresqlContainerLazy
 *   ) {
 *     // 在此上下文中进行多容器测试
 *     // 容器已在 Spring 属性注入时启动
 *     val redis = getRedisContainer()
 *     val postgres = getPostgresContainer()
 *   }
 * }
 * ```
 *
 * @author TrueNine
 * @since 2025-08-09
 */
interface ITestContainerBase {
  /**
   * 容器聚合函数
   *
   * 允许同时使用多个测试容器进行集成测试。 在执行块内可以通过上下文访问所有传入的容器。 容器已在 Spring 属性注入时启动，无需额外启动操作。
   *
   * @param containerLazies 需要聚合的容器懒加载实例
   * @param block 测试执行块，在 IContainersContext 上下文中执行
   * @return 测试执行块的返回值
   */
  fun <T> containers(vararg containerLazies: Lazy<out GenericContainer<*>>, block: IContainersContext.() -> T): T {
    val containers =
      containerLazies.map { lazy ->
        // 容器已在 @DynamicPropertySource 中启动，直接获取即可
        lazy.value
      }
    val context = ContainersContextImpl(containers)
    return context.block()
  }
}

/**
 * # 容器聚合上下文接口
 *
 * 在 `containers()` 函数中提供的上下文环境， 用于访问和操作多个测试容器。
 *
 * @author TrueNine
 * @since 2025-08-09
 */
interface IContainersContext {
  /**
   * 获取 Redis 容器
   *
   * @return Redis 容器实例，如果不存在则返回 null
   */
  fun getRedisContainer(): GenericContainer<*>?

  /**
   * 获取 PostgreSQL 容器
   *
   * @return PostgreSQL 容器实例，如果不存在则返回 null
   */
  fun getPostgresContainer(): PostgreSQLContainer<*>?

  /**
   * 获取 MySQL 容器
   *
   * @return MySQL 容器实例，如果不存在则返回 null
   */
  fun getMysqlContainer(): MySQLContainer<*>?

  /**
   * 获取 MinIO 容器
   *
   * @return MinIO 容器实例，如果不存在则返回 null
   */
  fun getMinioContainer(): GenericContainer<*>?

  /**
   * 获取所有容器
   *
   * @return 所有容器的列表
   */
  fun getAllContainers(): List<GenericContainer<*>>
}

/**
 * 容器聚合上下文的默认实现
 *
 * @param containers 传入的容器列表
 */
private class ContainersContextImpl(private val containers: List<GenericContainer<*>>) : IContainersContext {

  override fun getRedisContainer(): GenericContainer<*>? {
    return containers.find { container ->
      // 通过镜像名识别 Redis 容器
      container.dockerImageName.contains("redis", ignoreCase = true)
    }
  }

  override fun getPostgresContainer(): PostgreSQLContainer<*>? {
    return containers.find { it is PostgreSQLContainer<*> } as PostgreSQLContainer<*>?
  }

  override fun getMysqlContainer(): MySQLContainer<*>? {
    return containers.find { it is MySQLContainer<*> } as MySQLContainer<*>?
  }

  override fun getMinioContainer(): GenericContainer<*>? {
    return containers.find { container ->
      // 通过镜像名识别 MinIO 容器
      container.dockerImageName.contains("minio", ignoreCase = true)
    }
  }

  override fun getAllContainers(): List<GenericContainer<*>> {
    return containers.toList()
  }
}
