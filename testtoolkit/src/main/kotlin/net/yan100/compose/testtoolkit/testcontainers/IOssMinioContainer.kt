package net.yan100.compose.testtoolkit.testcontainers

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.Duration

/**
 * # MinIO 测试容器接口
 *
 * 该接口提供了 MinIO 测试容器的标准配置，用于对象存储集成测试环境。
 * 通过实现此接口，测试类可以自动获得配置好的 MinIO 测试实例。
 *
 * ## 特性
 * - 自动配置 MinIO 测试容器
 * - 提供标准的 MinIO 连接配置
 * - 支持 Spring Test 的动态属性注入
 * - 使用随机端口以避免端口冲突
 *
 * ## 使用方式
 * ```kotlin
 * @SpringBootTest
 * class YourTestClass : IOssMinioContainer {
 *   // 你的测试代码
 * }
 * ```
 * @author TrueNine
 * @since 2025-04-24
 * @see org.testcontainers.junit.jupiter.Testcontainers
 * @see org.testcontainers.containers.GenericContainer
 */
@Testcontainers
interface IOssMinioContainer {
  companion object {
    /**
     * MinIO 测试容器实例
     *
     * 预配置的 MinIO 容器，具有以下默认设置：
     * - 访问密钥: minioadmin
     * - 密钥: minioadmin
     * - API 端口: 随机分配
     * - 控制台端口: 随机分配
     */
    @JvmStatic
    val minio by lazy {
      GenericContainer(DockerImageName.parse("minio/minio:RELEASE.2025-04-22T22-12-26Z")).apply {
        withEnv("MINIO_ROOT_USER", "minioadmin")
        withEnv("MINIO_ROOT_PASSWORD", "minioadmin")
        withEnv("MINIO_CONSOLE_ADDRESS", ":9001")
        withCommand("server", "/data")
        withExposedPorts(9000, 9001)
        setWaitStrategy(
          Wait.forLogMessage(".*MinIO Object Storage Server.*\\n", 1)
            .withStartupTimeout(Duration.ofSeconds(10))
        )
      }
    }

    /**
     * Spring 测试环境动态属性配置
     *
     * 自动注入 MinIO 连接相关的配置属性到 Spring 测试环境中：
     * - 端点 URL
     * - 访问密钥
     * - 密钥
     *
     * @param registry Spring 动态属性注册器
     */
    @JvmStatic
    @DynamicPropertySource
    fun properties(registry: DynamicPropertyRegistry) {
      minio.start()
      val host = minio.host
      val port = minio.getMappedPort(9000)

      registry.add("compose.oss.base-url") { host }
      registry.add("compose.oss.expose-base-url") { "http://$host:$port" }
      registry.add("compose.oss.port") { port }
      registry.add("compose.oss.minio.enable-https") { false }
      registry.add("compose.oss.minio.access-key") { "minioadmin" }
      registry.add("compose.oss.minio.secret-key") { "minioadmin" }
    }
  }

  val minioContainer: GenericContainer<*>? get() = minio
}
