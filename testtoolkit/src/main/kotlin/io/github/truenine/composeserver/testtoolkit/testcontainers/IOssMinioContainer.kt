package io.github.truenine.composeserver.testtoolkit.testcontainers

import java.time.Duration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

/**
 * # MinIO 测试容器接口
 *
 * 该接口提供了 MinIO 测试容器的标准配置，用于对象存储集成测试环境。 通过实现此接口，测试类可以自动获得配置好的 MinIO 测试实例。
 *
 * ## ⚠️ 重要提示：容器重用与数据清理
 *
 * **默认情况下，为了提高测试运行效率，所有容器都是可重用的。** 这意味着容器会在多个测试之间共享，MinIO 中的对象和桶可能会残留。
 *
 * ### 数据清理责任
 * - **必须在测试中进行数据清理**：使用 `@BeforeEach` 或 `@AfterEach` 清理 MinIO 数据
 * - **推荐清理方式**：
 *     - 删除测试桶中的所有对象：`minioClient.removeObjects()`
 *     - 删除测试桶：`minioClient.removeBucket()`
 *     - 使用唯一的桶名称避免冲突：`test-bucket-${UUID.randomUUID()}`
 * - **不建议禁用重用**：虽然可以通过配置禁用容器重用，但会显著降低测试性能
 *
 * ### 清理示例
 *
 * ```kotlin
 * @BeforeEach
 * fun cleanupMinio() {
 *   // 清理测试桶
 *   if (minioClient.bucketExists(BucketExistsArgs.builder().bucket("test-bucket").build())) {
 *     // 删除桶中所有对象
 *     val objects = minioClient.listObjects(ListObjectsArgs.builder().bucket("test-bucket").build())
 *     objects.forEach {
 *       minioClient.removeObject(RemoveObjectArgs.builder().bucket("test-bucket").object(it.get().objectName()).build())
 *     }
 *     // 删除桶
 *     minioClient.removeBucket(RemoveBucketArgs.builder().bucket("test-bucket").build())
 *   }
 * }
 * ```
 *
 * ## 特性
 * - 自动配置 MinIO 测试容器
 * - 容器重用以提高性能
 * - 提供标准的 MinIO 连接配置
 * - 支持 Spring Test 的动态属性注入
 * - 使用随机端口以避免端口冲突
 * - 支持 S3 兼容 API
 *
 * ## 使用方式
 *
 * ```kotlin
 * @SpringBootTest
 * class YourTestClass : IOssMinioContainer {
 *
 *   @BeforeEach
 *   fun setup() {
 *     // 清理 MinIO 数据
 *     cleanupTestBuckets()
 *   }
 *
 *   @Test
 *   fun `测试对象存储功能`() {
 *     // 你的测试代码
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
interface IOssMinioContainer {
  companion object {
    /**
     * MinIO 测试容器实例
     *
     * 预配置的 MinIO 容器，设置可通过配置自定义：
     * - 访问密钥: 可配置，默认 minioadmin
     * - 密钥: 可配置，默认 minioadmin
     * - API 端口: 随机分配
     * - 控制台端口: 随机分配
     * - 版本: 可配置，默认 minio/minio:RELEASE.2025-04-22T22-12-26Z
     * - **容器重用**: 默认启用，多个测试共享同一容器实例
     *
     * ⚠️ **重要**: 由于容器重用，MinIO 中的对象和桶会在测试间残留，请确保在测试中进行适当的数据清理。
     */
    @JvmStatic
    val container by lazy {
      val config = TestcontainersConfigurationHolder.getTestcontainersProperties()
      GenericContainer(DockerImageName.parse(config.minio.image)).apply {
        withReuse(config.reuseAllContainers || config.minio.reuse)
        withEnv("MINIO_ROOT_USER", config.minio.accessKey)
        withEnv("MINIO_ROOT_PASSWORD", config.minio.secretKey)
        withEnv("MINIO_CONSOLE_ADDRESS", ":9001")
        withCommand("server", "/data")
        withExposedPorts(9000, 9001)
        setWaitStrategy(Wait.forLogMessage(".*MinIO Object Storage Server.*\\n", 1).withStartupTimeout(Duration.ofSeconds(10)))
        start()
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
      val config = TestcontainersConfigurationHolder.getTestcontainersProperties()
      val host = container.host
      val port = container.getMappedPort(9000)

      registry.add("compose.oss.base-url") { host }
      registry.add("compose.oss.expose-base-url") { "http://$host:$port" }
      registry.add("compose.oss.port") { port }
      registry.add("compose.oss.minio.enable-https") { false }
      registry.add("compose.oss.minio.access-key") { config.minio.accessKey }
      registry.add("compose.oss.minio.secret-key") { config.minio.secretKey }
    }
  }

  val minioContainer: GenericContainer<*>?
    get() = container
}
