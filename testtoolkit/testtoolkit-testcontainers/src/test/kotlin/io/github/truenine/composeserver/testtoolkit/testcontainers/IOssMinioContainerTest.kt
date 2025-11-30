package io.github.truenine.composeserver.testtoolkit.testcontainers

import io.github.truenine.composeserver.testtoolkit.SpringBootConfigurationPropertiesPrefixes
import jakarta.annotation.Resource
import java.net.InetSocketAddress
import java.net.Socket
import java.util.function.Supplier
import kotlin.test.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.test.context.DynamicPropertyRegistry

/**
 * MinIO test container integration tests.
 *
 * Verifies the configuration and runtime behavior of the MinIO test container:
 * - Container starts and runs correctly.
 * - Port mappings are configured correctly.
 * - Environment variables are set correctly.
 * - Spring properties are injected correctly.
 *
 * Coverage:
 * - Basic container behavior tests.
 * - Port mapping verification.
 * - Environment variable configuration verification.
 * - Spring property injection verification.
 *
 * Usage:
 * ```kotlin
 * @SpringBootTest
 * class YourTestClass : IOssMinioContainer {
 *   // your test code
 * }
 * ```
 *
 * @see IOssMinioContainer
 * @author TrueNine
 * @since 2024-04-24
 */
@SpringBootTest
@EnableAutoConfiguration(
  excludeName =
    [
      "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
      "org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration",
      "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
    ]
)
class IOssMinioContainerTest : IOssMinioContainer {
  lateinit var environment: Environment
    @Resource set

  @Nested
  inner class ContainerBasicTests {
    @Test
    fun verify_container_instance_exists_and_running() = minio {
      assertNotNull(it, "MinIO container instance should not be null")
      assertTrue(it.isRunning, "MinIO container should be in running state")
    }

    @Test
    fun verify_container_network_configuration() = minio {
      // Verify port mappings
      val apiPort = it.getMappedPort(9000)
      val consolePort = it.getMappedPort(9001)

      assertTrue(apiPort in 1024..65535, "API port mapping should be within a valid range")
      assertTrue(consolePort in 1024..65535, "Console port mapping should be within a valid range")
      assertNotEquals(apiPort, consolePort, "API port and console port should not be the same")
    }

    @ParameterizedTest
    @ValueSource(strings = ["MINIO_ROOT_USER", "MINIO_ROOT_PASSWORD", "MINIO_CONSOLE_ADDRESS"])
    fun verify_required_environment_variables_exist_and_configured(envVar: String) = minio {
      val envMap = it.envMap

      assertTrue(envMap.containsKey(envVar), "Environment variable $envVar must exist")
      assertNotNull(envMap[envVar], "Environment variable $envVar value must not be null")

      // Validate environment variable format and value
      when (envVar) {
        "MINIO_ROOT_USER" -> {
          assertEquals("minioadmin", envMap[envVar], "Access key configuration is incorrect")
          assertTrue(envMap[envVar]!!.isNotEmpty(), "Username must not be empty")
        }

        "MINIO_ROOT_PASSWORD" -> {
          assertEquals("minioadmin", envMap[envVar], "Secret key configuration is incorrect")
          assertTrue(envMap[envVar]!!.length >= 8, "Password length should be greater than or equal to 8 characters")
        }

        "MINIO_CONSOLE_ADDRESS" -> {
          assertEquals(":9001", envMap[envVar], "Console address configuration is incorrect")
          assertTrue(envMap[envVar]!!.startsWith(":"), "Console address should start with ':'")
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

      // Verify that all required properties are configured
      minio {
        val expectedProperties =
          mapOf(
            SpringBootConfigurationPropertiesPrefixes.OSS_MINIO_ENDPOINT to it.host,
            SpringBootConfigurationPropertiesPrefixes.OSS_MINIO_EXPOSED_BASE_URL to "http://${it.host}:${it.getMappedPort(9000)}",
            SpringBootConfigurationPropertiesPrefixes.OSS_MINIO_PORT to it.getMappedPort(9000).toString(),
            SpringBootConfigurationPropertiesPrefixes.OSS_MINIO_ACCESS_KEY to "minioadmin",
            SpringBootConfigurationPropertiesPrefixes.OSS_MINIO_SECRET_KEY to "minioadmin",
          )

        expectedProperties.forEach { (prop, expectedValue) ->
          assertTrue(registry.containsKey(prop), "Property $prop must exist")
          assertEquals(expectedValue, registry[prop], "Property $prop value is incorrect")
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
          assertNotNull(actualValue, "Missing property in environment: $prop")
          assertEquals(expectedValue, actualValue, "Environment property $prop value is incorrect")
        }

        // Special verification for port property (port is dynamically assigned)
        val portValue = environment.getProperty("compose.oss.port")
        assertNotNull(portValue, "Missing port configuration in environment")

        val portInt = portValue.toInt()
        assertTrue(portInt in 1024..65535, "Port value should be within valid range (actual: $portInt)")
        val socket = Socket()
        try {
          socket.connect(InetSocketAddress("localhost", portInt), 5000)
          assertTrue(socket.isConnected, "Port should be reachable")
        } finally {
          socket.close()
        }

        // Verify format of expose-base-url
        val exposeUrl = environment.getProperty("compose.oss.expose-base-url")
        assertNotNull(exposeUrl, "expose-base-url property should not be null")
        assertTrue(exposeUrl.startsWith("http://"), "Expose URL should start with http://")
        assertTrue(exposeUrl.contains(":$portInt"), "Expose URL should contain the correct port number")
      }
    }
  }
}
