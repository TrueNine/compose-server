package io.github.truenine.composeserver.testtoolkit.testcontainers

import io.github.truenine.composeserver.testtoolkit.SpringBootConfigurationPropertiesPrefixes
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
 * 该接口提供了 MinIO 测试容器的标准配置，用于对象存储集成测试环境。 通过实现此接口，测试类可以自动获得配置好的 MinIO 测试实例，并可以使用扩展函数进行便捷测试。
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
 * ### 传统方式（向后兼容）
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
 * ### 扩展函数方式（推荐）
 *
 * ```kotlin
 * @SpringBootTest
 * class YourTestClass : IOssMinioContainer {
 *
 *   @Test
 *   fun `测试对象存储功能`() = minio(resetToInitialState = true) { container ->
 *     // 容器会自动重置到初始状态，无需手动清理
 *     // container 是当前的 MinIO 容器实例
 *     // 创建桶和上传对象...
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
interface IOssMinioContainer : ITestContainerBase {
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
     * - **延迟启动**: 容器在首次使用时才启动
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
        // 移除 start() 调用，容器在使用时才启动
      }
    }

    /**
     * MinIO 容器的懒加载实例
     *
     * 用于 containers() 聚合函数，不会立即创建容器，只有在被调用时才创建。
     *
     * @return 懒加载的 MinIO 容器实例
     */
    @JvmStatic val minioContainerLazy: Lazy<GenericContainer<*>> by lazy { lazy { container } }

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
      // 确保容器已启动（为 @DynamicPropertySource 提供支持）
      if (!container.isRunning) {
        container.start()
      }

      val config = TestcontainersConfigurationHolder.getTestcontainersProperties()
      val host = container.host
      val port = container.getMappedPort(9000)

      registry.add(SpringBootConfigurationPropertiesPrefixes.OSS_MINIO_ENDPOINT) { host }
      registry.add(SpringBootConfigurationPropertiesPrefixes.OSS_MINIO_EXPOSED_BASE_URL) { "http://$host:$port" }
      registry.add(SpringBootConfigurationPropertiesPrefixes.OSS_MINIO_PORT) { port }
      registry.add(SpringBootConfigurationPropertiesPrefixes.OSS_MINIO_ACCESS_KEY) { config.minio.accessKey }
      registry.add(SpringBootConfigurationPropertiesPrefixes.OSS_MINIO_SECRET_KEY) { config.minio.secretKey }
    }
  }

  /**
   * MinIO 容器扩展函数
   *
   * 提供便捷的 MinIO 容器测试方式，支持自动数据重置。 容器将在首次使用时自动启动。
   *
   * @param resetToInitialState 是否重置到初始状态（清空所有测试相关的桶和对象），默认为 true
   * @param block 测试执行块，接收当前 MinIO 容器实例作为参数
   * @return 测试执行块的返回值
   */
  fun <T> minio(resetToInitialState: Boolean = true, block: (GenericContainer<*>) -> T): T {
    // 确保容器已启动
    if (!container.isRunning) {
      container.start()
    }

    if (resetToInitialState) {
      // 重置 MinIO 到初始状态 - 清空所有测试相关的桶
      try {
        // 使用 mc (MinIO Client) 命令来管理桶
        // 首先配置 mc 客户端
        container.execInContainer("mc", "alias", "set", "testminio", "http://localhost:9000", "minioadmin", "minioadmin")

        // 列出所有桶
        val listResult = container.execInContainer("mc", "ls", "testminio")
        if (listResult.exitCode == 0 && listResult.stdout.isNotEmpty()) {
          val buckets =
            listResult.stdout
              .split("\n")
              .filter { it.trim().isNotEmpty() }
              .mapNotNull { line ->
                // 提取桶名称（格式类似：[2023-01-01 00:00:00 UTC]     0B bucket-name/）
                val bucketMatch = Regex("\\s+\\d+B\\s+(.+?)/?$").find(line.trim())
                bucketMatch?.groupValues?.get(1)
              }
              .filter { bucketName ->
                // 只删除测试相关的桶（以 test- 或 combined- 开头）
                bucketName.startsWith("test-") || bucketName.startsWith("combined-")
              }

          // 删除测试桶
          buckets.forEach { bucketName ->
            try {
              // 强制递归删除桶及其中的所有对象
              container.execInContainer("mc", "rb", "--force", "testminio/$bucketName")
            } catch (e: Exception) {
              org.slf4j.LoggerFactory.getLogger(IOssMinioContainer::class.java).debug("删除测试桶 {} 时出现异常（可能已不存在）: {}", bucketName, e.message)
            }
          }
        }
      } catch (e: Exception) {
        // 如果清理失败，记录警告但继续执行测试
        org.slf4j.LoggerFactory.getLogger(IOssMinioContainer::class.java).warn("无法重置 MinIO 容器到初始状态: {}", e.message)
      }
    }

    return block(container)
  }
}
