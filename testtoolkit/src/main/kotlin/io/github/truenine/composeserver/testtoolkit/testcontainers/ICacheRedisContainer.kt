package io.github.truenine.composeserver.testtoolkit.testcontainers

import java.time.Duration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

/**
 * # Redis 测试容器接口
 *
 * 该接口提供了 Redis 测试容器的标准配置，用于缓存集成测试环境。 通过实现此接口，测试类可以自动获得配置好的 Redis 测试实例，并可以使用扩展函数进行便捷测试。
 *
 * ## ⚠️ 重要提示：容器重用与数据清理
 *
 * **默认情况下，为了提高测试运行效率，所有容器都是可重用的。** 这意味着容器会在多个测试之间共享，数据可能会残留。
 *
 * ### 数据清理责任
 * - **必须在测试中进行数据清理**：使用 `@BeforeEach` 或 `@AfterEach` 清理 Redis 数据
 * - **推荐清理方式**：`FLUSHALL` 或 `FLUSHDB` 命令清空所有键
 * - **不建议禁用重用**：虽然可以通过配置禁用容器重用，但会显著降低测试性能
 *
 * ### 清理示例
 *
 * ```kotlin
 * @BeforeEach
 * fun cleanupRedis() {
 *   redisTemplate.execute { connection ->
 *     connection.flushAll()
 *     null
 *   }
 * }
 * ```
 *
 * ## 特性
 * - 自动配置 Redis 测试容器
 * - 容器重用以提高性能
 * - 提供标准的 Redis 连接配置
 * - 支持 Spring Test 的动态属性注入
 * - 使用随机端口以避免端口冲突
 *
 * ## 使用方式
 *
 * ### 传统方式（向后兼容）
 *
 * ```kotlin
 * @SpringBootTest
 * class YourTestClass : ICacheRedisContainer {
 *
 *   @BeforeEach
 *   fun setup() {
 *     // 清理 Redis 数据
 *     redisTemplate.execute { it.flushAll(); null }
 *   }
 *
 *   @Test
 *   fun `测试缓存功能`() {
 *     // 你的测试代码
 *   }
 * }
 * ```
 *
 * ### 扩展函数方式（推荐）
 *
 * ```kotlin
 * @SpringBootTest
 * class YourTestClass : ICacheRedisContainer {
 *
 *   @Test
 *   fun `测试缓存功能`() = redis(resetToInitialState = true) { container ->
 *     // 容器会自动重置到初始状态，无需手动清理
 *     // container 是当前的 Redis 容器实例
 *     redisTemplate.opsForValue().set("key", "value")
 *     // 测试逻辑...
 *   }
 * }
 * ```
 *
 * @see org.testcontainers.junit.jupiter.Testcontainers
 * @see org.testcontainers.containers.GenericContainer
 * @author TrueNine
 * @since 2025-04-24
 */
@Testcontainers
interface ICacheRedisContainer : ITestContainerBase {
  companion object {
    /**
     * Redis 测试容器实例
     *
     * 预配置的 Redis 容器，具有以下默认设置：
     * - 端口: 6379 (随机映射)
     * - 版本: 可通过配置自定义，默认 7.4.2-alpine3.21
     * - 无密码认证
     * - **容器重用**: 默认启用，多个测试共享同一容器实例
     * - **延迟启动**: 容器在首次使用时才启动
     *
     * ⚠️ **重要**: 由于容器重用，数据会在测试间残留，请确保在测试中进行适当的数据清理。
     */
    @JvmStatic
    val container by lazy {
      val config = TestcontainersConfigurationHolder.getTestcontainersProperties()
      GenericContainer(DockerImageName.parse(config.redis.image)).apply {
        withReuse(config.reuseAllContainers || config.redis.reuse)
        withExposedPorts(6379)
        setWaitStrategy(Wait.forLogMessage(".*Ready to accept connections.*\\n", 1).withStartupTimeout(Duration.ofSeconds(10)))

        // 配置停止超时时间 - TestContainers 没有 withStopTimeout 方法，我们可以通过其他方式处理生命周期

        // 移除 start() 调用，容器在使用时才启动
      }
    }

    /**
     * Redis 容器的懒加载实例
     *
     * 用于 containers() 聚合函数，不会立即创建容器，只有在被调用时才创建。
     *
     * @return 懒加载的 Redis 容器实例
     */
    @JvmStatic val redisContainerLazy: Lazy<GenericContainer<*>> by lazy { lazy { container } }

    /**
     * Spring 测试环境动态属性配置
     *
     * 自动注入 Redis 连接相关的配置属性到 Spring 测试环境中：
     * - 主机地址
     * - 端口
     *
     * @param registry Spring 动态属性注册器
     */
    @JvmStatic
    @DynamicPropertySource
    fun properties(registry: DynamicPropertyRegistry) {
      // 确保容器已启动（为 @DynamicPropertySource 提供支持）
      if (!container.isRunning) {
        container.start()
      }

      val host = container.host
      val port = container.getMappedPort(6379)

      registry.add("spring.data.redis.host") { host }
      registry.add("spring.data.redis.port") { port }
    }
  }

  /**
   * Redis 容器扩展函数
   *
   * 提供便捷的 Redis 容器测试方式，支持自动数据重置。 容器将在首次使用时自动启动。
   *
   * @param resetToInitialState 是否重置到初始状态（清空所有数据），默认为 true
   * @param block 测试执行块，接收当前 Redis 容器实例作为参数
   * @return 测试执行块的返回值
   */
  fun <T> redis(resetToInitialState: Boolean = true, block: (GenericContainer<*>) -> T): T {
    // 确保容器已启动
    if (!container.isRunning) {
      container.start()
    }

    if (resetToInitialState) {
      // 重置 Redis 到初始状态 - 清空所有数据
      try {
        // 使用容器执行 Redis FLUSHALL 命令
        container.execInContainer("redis-cli", "FLUSHALL")
      } catch (e: Exception) {
        // 如果清理失败，记录警告但继续执行测试
        org.slf4j.LoggerFactory.getLogger(ICacheRedisContainer::class.java).warn("无法重置 Redis 容器到初始状态: {}", e.message)
      }
    }

    return block(container)
  }
}
