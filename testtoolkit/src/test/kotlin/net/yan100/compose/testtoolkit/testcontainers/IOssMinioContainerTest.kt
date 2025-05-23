package net.yan100.compose.testtoolkit.testcontainers

import jakarta.annotation.Resource
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.test.context.DynamicPropertyRegistry
import java.util.function.Supplier
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * # MinIO 测试容器集成测试
 *
 * 该测试类验证 MinIO 测试容器的配置和运行状态，确保：
 * - 容器正确启动和运行
 * - 端口映射配置正确
 * - 环境变量设置正确
 * - Spring 属性注入正确
 *
 * ## 测试覆盖范围
 * - 容器基本功能测试
 * - 端口映射验证
 * - 环境变量配置验证
 * - Spring 属性注入验证
 *
 * ## 使用方式
 * ```kotlin
 * @SpringBootTest
 * class YourTestClass : IOssMinioContainer {
 *   // 你的测试代码
 * }
 * ```
 *
 * @author TrueNine
 * @since 2024-04-24
 * @see IOssMinioContainer
 */
@SpringBootTest
@DisplayName("MinIO 测试容器集成测试")
class IOssMinioContainerTest : IOssMinioContainer {
  lateinit var environment: Environment @Resource set

  @Nested
  @DisplayName("容器基本功能测试")
  inner class ContainerBasicTests {
    @Test
    @DisplayName("验证容器实例存在且正在运行")
    fun `验证容器实例存在且正在运行`() {
      assertNotNull(minioContainer, "MinIO 容器实例不应为空")
      assertTrue(minioContainer!!.isRunning, "MinIO 容器应该处于运行状态")
    }

    @Test
    @DisplayName("验证容器网络配置正确")
    fun `验证容器网络配置正确`() {
      val container = minioContainer!!

      // 验证端口映射
      val apiPort = container.getMappedPort(9000)
      val consolePort = container.getMappedPort(9001)

      assertTrue(apiPort in 1024..65535, "API 端口映射应在有效范围内")
      assertTrue(consolePort in 1024..65535, "控制台端口映射应在有效范围内")
      assertNotEquals(apiPort, consolePort, "API 端口和控制台端口不应相同")
    }

    @ParameterizedTest
    @ValueSource(strings = ["MINIO_ROOT_USER", "MINIO_ROOT_PASSWORD", "MINIO_CONSOLE_ADDRESS"])
    @DisplayName("验证必需的环境变量存在且配置正确")
    fun `验证必需的环境变量存在且配置正确`(envVar: String) {
      val container = minioContainer!!
      val envMap = container.envMap

      assertTrue(envMap.containsKey(envVar), "环境变量 $envVar 必须存在")
      assertNotNull(envMap[envVar], "环境变量 $envVar 的值不能为空")

      when (envVar) {
        "MINIO_ROOT_USER" -> assertEquals("minioadmin", envMap[envVar], "访问密钥配置不正确")
        "MINIO_ROOT_PASSWORD" -> assertEquals("minioadmin", envMap[envVar], "密钥配置不正确")
        "MINIO_CONSOLE_ADDRESS" -> assertEquals(":9001", envMap[envVar], "控制台地址配置不正确")
      }
    }
  }

  @Nested
  @DisplayName("Spring 属性注入测试")
  inner class SpringPropertiesTests {
    @Test
    @DisplayName("验证动态属性注册正确")
    fun `验证动态属性注册正确`() {
      val registry = mutableMapOf<String, String>()
      val mockRegistry = object : DynamicPropertyRegistry {
        override fun add(name: String, valueSupplier: Supplier<in Any>) {
          registry[name] = valueSupplier.get().toString()
        }
      }

      IOssMinioContainer.properties(mockRegistry)

      // 验证所有必需的属性都已配置
      val expectedProperties = mapOf(
        "compose.oss.base-url" to "localhost",
        "compose.oss.expose-base-url" to "http://localhost:${minioContainer!!.getMappedPort(9000)}",
        "compose.oss.port" to minioContainer!!.getMappedPort(9000).toString(),
        "compose.oss.minio.enable-https" to "false",
        "compose.oss.minio.access-key" to "minioadmin",
        "compose.oss.minio.secret-key" to "minioadmin"
      )

      expectedProperties.forEach { (prop, expectedValue) ->
        assertTrue(registry.containsKey(prop), "属性 $prop 必须存在")
        assertEquals(expectedValue, registry[prop], "属性 $prop 的值配置不正确")
      }
    }

    @Test
    @DisplayName("验证环境变量注入正确")
    fun `验证环境变量注入正确`() {
      val expectedProperties = mapOf(
        "compose.oss.base-url" to "localhost",
        "compose.oss.expose-base-url" to "http://localhost:${minioContainer!!.getMappedPort(9000)}",
        "compose.oss.minio.enable-https" to "false",
        "compose.oss.minio.access-key" to "minioadmin",
        "compose.oss.minio.secret-key" to "minioadmin"
      )

      expectedProperties.forEach { (prop, expectedValue) ->
        val actualValue = environment.getProperty(prop)
        assertNotNull(actualValue, "环境变量中缺少属性: $prop")
        assertEquals(expectedValue, actualValue, "环境变量 $prop 的值配置不正确")
      }

      // 特殊验证端口属性（因为端口是动态分配的）
      val portValue = environment.getProperty("compose.oss.port")
      assertNotNull(portValue, "环境变量中缺少端口配置")
      assertTrue(portValue.toInt() in 1024..65535, "端口值应在有效范围内")
    }
  }
} 
