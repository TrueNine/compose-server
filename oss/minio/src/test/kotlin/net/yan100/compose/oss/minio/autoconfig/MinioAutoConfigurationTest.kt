package net.yan100.compose.oss.minio.autoconfig

import io.minio.MinioClient
import jakarta.annotation.Resource
import net.yan100.compose.oss.minio.MinioClientWrapper
import org.springframework.beans.factory.getBean
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringBootTest
class MinioAutoConfigurationTest {
  companion object {
    private val minioContainer =
      GenericContainer("minio/minio:latest")
        .withExposedPorts(9000, 9001)
        .withEnv("MINIO_ROOT_USER", "minio")
        .withEnv("MINIO_ROOT_PASSWORD", "minio123")
        .withCommand(
          "server",
          "/data",
          "--console-address",
          ":9001",
          "--address",
          ":9000",
        )
        .waitingFor(
          org.testcontainers.containers.wait.strategy.Wait.forLogMessage(
            ".*MinIO Object Storage Server.*",
            1,
          )
            .withStartupTimeout(java.time.Duration.ofSeconds(30))
        )

    init {
      minioContainer.start()
    }

    @JvmStatic
    @DynamicPropertySource
    fun properties(registry: DynamicPropertyRegistry) {
      registry.add("compose.oss.minio.endpoint") { "http://localhost" }
      registry.add("compose.oss.minio.endpoint-port") {
        minioContainer.getMappedPort(9000)
      }
      registry.add("compose.oss.minio.access-key") { "minio" }
      registry.add("compose.oss.minio.secret-key") { "minio123" }
      registry.add("compose.oss.minio.exposed-base-url") {
        "http://localhost:${minioContainer.getMappedPort(9000)}"
      }
    }
  }

  lateinit var ctx: ApplicationContext
    @Resource set

  @Test
  fun `确保 初始化了 client wrapper`() {
    val wrapper = ctx.getBean<MinioClientWrapper>()
    assertNotNull(wrapper)
    assertNotNull(wrapper.exposedBaseUrl)
  }

  @Test
  fun `确保 只有 bean 的情况下，初始化了 minio 客户端`() {
    val client = ctx.getBean<MinioClient>()
    assertNotNull(client)
  }

  @Test
  fun `测试 Minio 连接是否正常`() {
    val wrapper = ctx.getBean<MinioClientWrapper>()
    assertNotNull(wrapper)
    kotlin.test.assertTrue(wrapper.isConnected, "Minio 服务连接失败")

    // 测试基本操作
    val testBucketName = "connection-test-bucket"
    wrapper.createBucketByName(testBucketName)
    kotlin.test.assertTrue(
      wrapper.existsBucketByName(testBucketName),
      "创建测试 bucket 失败",
    )

    // 清理测试数据
    val buckets = wrapper.fetchAllBucketNames()
    kotlin.test.assertTrue(
      buckets.contains(testBucketName),
      "无法在 bucket 列表中找到测试 bucket",
    )
  }
}
