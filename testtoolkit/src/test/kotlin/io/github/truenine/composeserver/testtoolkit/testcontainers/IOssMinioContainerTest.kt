package io.github.truenine.composeserver.testtoolkit.testcontainers

import jakarta.annotation.Resource
import java.net.InetSocketAddress
import java.net.Socket
import java.util.function.Supplier
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.test.context.DynamicPropertyRegistry

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
 *
 * ```kotlin
 * @SpringBootTest
 * class YourTestClass : IOssMinioContainer {
 *   // 你的测试代码
 * }
 * ```
 *
 * @see IOssMinioContainer
 * @author TrueNine
 * @since 2024-04-24
 */
@SpringBootTest
@EnableAutoConfiguration(
  exclude = [DataSourceAutoConfiguration::class, DataSourceTransactionManagerAutoConfiguration::class, HibernateJpaAutoConfiguration::class]
)
class IOssMinioContainerTest : IOssMinioContainer {
  lateinit var environment: Environment
    @Resource set

  @Nested
  inner class ContainerBasicTests {
    @Test
    fun verify_container_instance_exists_and_running() = minio {
      assertNotNull(it, "MinIO 容器实例不应为空")
      assertTrue(it.isRunning, "MinIO 容器应该处于运行状态")
    }

    @Test
    fun verify_container_network_configuration() = minio {
      // 验证端口映射
      val apiPort = it.getMappedPort(9000)
      val consolePort = it.getMappedPort(9001)

      assertTrue(apiPort in 1024..65535, "API 端口映射应在有效范围内")
      assertTrue(consolePort in 1024..65535, "控制台端口映射应在有效范围内")
      assertNotEquals(apiPort, consolePort, "API 端口和控制台端口不应相同")
    }

    @ParameterizedTest
    @ValueSource(strings = ["MINIO_ROOT_USER", "MINIO_ROOT_PASSWORD", "MINIO_CONSOLE_ADDRESS"])
    fun verify_required_environment_variables_exist_and_configured(envVar: String) = minio {
      val envMap = it.envMap

      assertTrue(envMap.containsKey(envVar), "环境变量 $envVar 必须存在")
      assertNotNull(envMap[envVar], "环境变量 $envVar 的值不能为空")

      // 验证环境变量格式和值
      when (envVar) {
        "MINIO_ROOT_USER" -> {
          assertEquals("minioadmin", envMap[envVar], "访问密钥配置不正确")
          assertTrue(envMap[envVar]!!.isNotEmpty(), "用户名不应为空")
        }

        "MINIO_ROOT_PASSWORD" -> {
          assertEquals("minioadmin", envMap[envVar], "密钥配置不正确")
          assertTrue(envMap[envVar]!!.length >= 8, "密码长度应详大于等于8位")
        }

        "MINIO_CONSOLE_ADDRESS" -> {
          assertEquals(":9001", envMap[envVar], "控制台地址配置不正确")
          assertTrue(envMap[envVar]!!.startsWith(":"), "控制台地址应以冒号开头")
        }
      }
    }
  }

  @Nested
  inner class SpringPropertiesTests {
    @Test
    fun verify_dynamic_property_registration() {
      val registry = mutableMapOf<String, String>()
      val mockRegistry =
        object : DynamicPropertyRegistry {
          override fun add(name: String, valueSupplier: Supplier<in Any>) {
            registry[name] = valueSupplier.get().toString()
          }
        }

      IOssMinioContainer.properties(mockRegistry)

      // 验证所有必需的属性都已配置
      minio {
        val expectedProperties =
          mapOf(
            "compose.oss.base-url" to it.host,
            "compose.oss.expose-base-url" to "http://${it.host}:${it.getMappedPort(9000)}",
            "compose.oss.port" to it.getMappedPort(9000).toString(),
            "compose.oss.minio.enable-https" to "false",
            "compose.oss.minio.access-key" to "minioadmin",
            "compose.oss.minio.secret-key" to "minioadmin",
          )

        expectedProperties.forEach { (prop, expectedValue) ->
          assertTrue(registry.containsKey(prop), "属性 $prop 必须存在")
          assertEquals(expectedValue, registry[prop], "属性 $prop 的值配置不正确")
        }
      }

      @Test
      fun verify_environment_variable_injection() = minio {
        val expectedProperties =
          mapOf(
            "compose.oss.base-url" to it.host,
            "compose.oss.expose-base-url" to "http://${it.host}:${it.getMappedPort(9000)}",
            "compose.oss.minio.enable-https" to "false",
            "compose.oss.minio.access-key" to "minioadmin",
            "compose.oss.minio.secret-key" to "minioadmin",
          )

        expectedProperties.forEach { (prop, expectedValue) ->
          val actualValue = environment.getProperty(prop)
          assertNotNull(actualValue, "环境变量中缺少属性: $prop")
          assertEquals(expectedValue, actualValue, "环境变量 $prop 的值配置不正确")
        }

        // 特殊验证端口属性（因为端口是动态分配的）
        val portValue = environment.getProperty("compose.oss.port")
        assertNotNull(portValue, "环境变量中缺少端口配置")

        val portInt = portValue.toInt()
        assertTrue(portInt in 1024..65535, "端口值应在有效范围内 (actual: $portInt)")
        val socket = Socket()
        try {
          socket.connect(InetSocketAddress("localhost", portInt), 5000)
          assertTrue(socket.isConnected, "端口应详可访问")
        } finally {
          socket.close()
        }

        // 验证 expose-base-url 的格式
        val exposeUrl = environment.getProperty("compose.oss.expose-base-url")
        assertNotNull(exposeUrl, "expose-base-url 属性不应为 null")
        assertTrue(exposeUrl.startsWith("http://"), "expose URL 应以 http:// 开头")
        assertTrue(exposeUrl.contains(":$portInt"), "expose URL 应包含正确的端口号")
      }
    }
  }
}
