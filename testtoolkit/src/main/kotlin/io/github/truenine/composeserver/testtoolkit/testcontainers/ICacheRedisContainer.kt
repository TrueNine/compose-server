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
 * 该接口提供了 Redis 测试容器的标准配置，用于缓存集成测试环境。 通过实现此接口，测试类可以自动获得配置好的 Redis 测试实例。
 *
 * ## 特性
 * - 自动配置 Redis 测试容器
 * - 提供标准的 Redis 连接配置
 * - 支持 Spring Test 的动态属性注入
 * - 使用随机端口以避免端口冲突
 *
 * ## 使用方式
 *
 * ```kotlin
 * @SpringBootTest
 * class YourTestClass : ICacheRedisContainer {
 *   // 你的测试代码
 * }
 * ```
 *
 * @see org.testcontainers.junit.jupiter.Testcontainers
 * @see org.testcontainers.containers.GenericContainer
 * @author TrueNine
 * @since 2025-04-24
 */
@Testcontainers
interface ICacheRedisContainer {
  companion object {
    /**
     * Redis 测试容器实例
     *
     * 预配置的 Redis 容器，具有以下默认设置：
     * - 端口: 6379 (随机映射)
     * - 版本: 可通过配置自定义，默认 7.4.2-alpine3.21
     * - 无密码认证
     */
    @JvmStatic
    val container by lazy {
      val config = TestcontainersConfigurationHolder.getTestcontainersProperties()
      GenericContainer(DockerImageName.parse(config.redis.image)).apply {
        withReuse(true)
        withExposedPorts(6379)
        setWaitStrategy(Wait.forLogMessage(".*Ready to accept connections.*\\n", 1).withStartupTimeout(Duration.ofSeconds(10)))
        start()
      }
    }

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

      val host = container.host
      val port = container.getMappedPort(6379)

      registry.add("spring.data.redis.host") { host }
      registry.add("spring.data.redis.port") { port }
    }
  }

  val redisContainer: GenericContainer<*>?
    get() = container
}
