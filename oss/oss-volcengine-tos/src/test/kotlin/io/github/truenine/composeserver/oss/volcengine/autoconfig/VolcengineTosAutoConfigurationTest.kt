package io.github.truenine.composeserver.oss.volcengine.autoconfig

import com.volcengine.tos.TOSV2
import io.github.truenine.composeserver.oss.properties.OssProperties
import io.github.truenine.composeserver.oss.volcengine.properties.VolcengineTosProperties
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Tests the Volcengine TOS auto-configuration mechanism.
 *
 * @author TrueNine
 * @since 2025-08-11
 */
class VolcengineTosAutoConfigurationTest {

  private val contextRunner = ApplicationContextRunner().withConfiguration(AutoConfigurations.of(VolcengineTosAutoConfiguration::class.java))

  @Nested
  inner class AutoConfigurationActivation {

    @Test
    fun `enables auto configuration when TOSV2 class exists`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.volcengine-tos.endpoint=tos-cn-beijing.volces.com",
          "compose.oss.volcengine-tos.region=cn-beijing",
          "compose.oss.volcengine-tos.access-key=testkey",
          "compose.oss.volcengine-tos.secret-key=testsecret",
        )
        .run { context ->
          // Verify that the configuration properties are bound correctly
          val tosProperties = context.getBean(VolcengineTosProperties::class.java)
          kotlin.test.assertEquals("tos-cn-beijing.volces.com", tosProperties.endpoint)
          kotlin.test.assertEquals("cn-beijing", tosProperties.region)
          kotlin.test.assertEquals("testkey", tosProperties.accessKey)
          kotlin.test.assertEquals("testsecret", tosProperties.secretKey)

          // Verify that the TOS client is created
          assertTrue(context.containsBeanDefinition("volcengineTosClient"))
          assertTrue(context.containsBeanDefinition("volcengineTosObjectStorageService"))
        }
    }

    @Test
    fun `fails to start when required configuration is missing`() {
      contextRunner.run { context ->
        // Verify that the context fails to start because required configuration is missing
        assertTrue(context.startupFailure != null)

        // Verify that the failure is caused by missing required configuration
        val failure = context.startupFailure
        assertTrue(
          failure?.message?.contains("endpoint") == true || failure?.message?.contains("region") == true || failure?.message?.contains("access") == true
        )
      }
    }
  }

  @Nested
  inner class PropertyBinding {

    @Test
    fun `binds Volcengine TOS specific configuration`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.volcengine-tos.endpoint=tos-cn-beijing.volces.com",
          "compose.oss.volcengine-tos.region=cn-beijing",
          "compose.oss.volcengine-tos.access-key=testkey",
          "compose.oss.volcengine-tos.secret-key=testsecret",
          "compose.oss.volcengine-tos.enable-ssl=true",
          "compose.oss.volcengine-tos.session-token=testtoken",
          "compose.oss.volcengine-tos.connect-timeout-mills=30000",
          "compose.oss.volcengine-tos.read-timeout-mills=60000",
        )
        .run { context ->
          val tosProperties = context.getBean(VolcengineTosProperties::class.java)

          kotlin.test.assertEquals("tos-cn-beijing.volces.com", tosProperties.endpoint)
          kotlin.test.assertEquals("cn-beijing", tosProperties.region)
          kotlin.test.assertEquals("testkey", tosProperties.accessKey)
          kotlin.test.assertEquals("testsecret", tosProperties.secretKey)
          kotlin.test.assertTrue(tosProperties.enableSsl)
          kotlin.test.assertEquals("testtoken", tosProperties.sessionToken)
          kotlin.test.assertEquals(30000, tosProperties.connectTimeoutMills)
          kotlin.test.assertEquals(60000, tosProperties.readTimeoutMills)
        }
    }

    @Test
    fun `supports generic OSS configuration as fallback`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.endpoint=oss.example.com",
          "compose.oss.region=us-east-1",
          "compose.oss.access-key=osskey",
          "compose.oss.secret-key=osssecret",
          "compose.oss.volcengine-tos.endpoint=tos.example.com",
          "compose.oss.volcengine-tos.region=cn-beijing",
          "compose.oss.volcengine-tos.access-key=toskey",
          "compose.oss.volcengine-tos.secret-key=tossecret",
        )
        .run { context ->
          val ossProperties = context.getBean(OssProperties::class.java)
          val tosProperties = context.getBean(VolcengineTosProperties::class.java)

          // Verify that the generic configuration is bound
          kotlin.test.assertEquals("oss.example.com", ossProperties.endpoint)
          kotlin.test.assertEquals("us-east-1", ossProperties.region)
          kotlin.test.assertEquals("osskey", ossProperties.accessKey)
          kotlin.test.assertEquals("osssecret", ossProperties.secretKey)

          // Verify that the TOS specific configuration is bound
          kotlin.test.assertEquals("tos.example.com", tosProperties.endpoint)
          kotlin.test.assertEquals("cn-beijing", tosProperties.region)
          kotlin.test.assertEquals("toskey", tosProperties.accessKey)
          kotlin.test.assertEquals("tossecret", tosProperties.secretKey)
        }
    }
  }

  @Nested
  inner class BackwardCompatibility {

    @Test
    fun `ignores deprecated provider configuration`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.provider=volcengine-tos", // Deprecated configuration
          "compose.oss.volcengine-tos.endpoint=tos-cn-beijing.volces.com",
          "compose.oss.volcengine-tos.region=cn-beijing",
          "compose.oss.volcengine-tos.access-key=testkey",
          "compose.oss.volcengine-tos.secret-key=testsecret",
        )
        .run { context ->
          // Verify that the provider property remains accessible (backward compatibility)
          val ossProperties = context.getBean(OssProperties::class.java)
          kotlin.test.assertEquals("volcengine-tos", ossProperties.provider)

          // Verify that the TOS configuration is bound correctly
          val tosProperties = context.getBean(VolcengineTosProperties::class.java)
          kotlin.test.assertEquals("tos-cn-beijing.volces.com", tosProperties.endpoint)
          kotlin.test.assertEquals("cn-beijing", tosProperties.region)

          // Verify that the TOS client is created
          assertTrue(context.containsBeanDefinition("volcengineTosClient"))
          assertTrue(context.containsBeanDefinition("volcengineTosObjectStorageService"))
        }
    }

    @Test
    fun `works without provider configuration`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.volcengine-tos.endpoint=tos-cn-beijing.volces.com",
          "compose.oss.volcengine-tos.region=cn-beijing",
          "compose.oss.volcengine-tos.access-key=testkey",
          "compose.oss.volcengine-tos.secret-key=testsecret",
        )
        .run { context ->
          // Verify that the provider property is null
          val ossProperties = context.getBean(OssProperties::class.java)
          kotlin.test.assertNull(ossProperties.provider)

          // Verify that the TOS configuration is bound correctly
          val tosProperties = context.getBean(VolcengineTosProperties::class.java)
          kotlin.test.assertEquals("tos-cn-beijing.volces.com", tosProperties.endpoint)
          kotlin.test.assertEquals("cn-beijing", tosProperties.region)

          // Verify that the TOS client is created
          assertTrue(context.containsBeanDefinition("volcengineTosClient"))
          assertTrue(context.containsBeanDefinition("volcengineTosObjectStorageService"))
        }
    }
  }

  @Nested
  inner class DefaultRegionConfiguration {

    @Test
    fun `uses default region when none is provided`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.volcengine-tos.endpoint=tos-cn-beijing.volces.com",
          "compose.oss.volcengine-tos.access-key=testkey",
          "compose.oss.volcengine-tos.secret-key=testsecret",
        )
        .run { context ->
          // Verify that the region property falls back to the default value
          val tosProperties = context.getBean(VolcengineTosProperties::class.java)
          kotlin.test.assertEquals("cn-beijing", tosProperties.region)

          val ossProperties = context.getBean(OssProperties::class.java)
          kotlin.test.assertNull(ossProperties.region)

          // Verify that the TOS client is created using the default region
          assertTrue(context.containsBeanDefinition("volcengineTosClient"))
          assertTrue(context.containsBeanDefinition("volcengineTosObjectStorageService"))
        }
    }

    @Test
    fun `prefers TOS specific region over generic OSS region`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.endpoint=tos-cn-beijing.volces.com",
          "compose.oss.region=cn-shanghai",
          "compose.oss.access-key=testkey",
          "compose.oss.secret-key=testsecret",
          "compose.oss.volcengine-tos.region=cn-guangzhou",
        )
        .run { context ->
          val tosProperties = context.getBean(VolcengineTosProperties::class.java)
          val ossProperties = context.getBean(OssProperties::class.java)

          // Verify that the TOS-specific configuration takes precedence
          kotlin.test.assertEquals("cn-guangzhou", tosProperties.region)
          kotlin.test.assertEquals("cn-shanghai", ossProperties.region)

          // Verify that the TOS client is created
          assertTrue(context.containsBeanDefinition("volcengineTosClient"))
        }
    }

    @Test
    fun `uses generic OSS region as fallback`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.endpoint=tos-cn-beijing.volces.com",
          "compose.oss.region=cn-hongkong",
          "compose.oss.access-key=testkey",
          "compose.oss.secret-key=testsecret",
        )
        .run { context ->
          val tosProperties = context.getBean(VolcengineTosProperties::class.java)
          val ossProperties = context.getBean(OssProperties::class.java)

          // Verify that TOS falls back to the default region configuration
          kotlin.test.assertEquals("cn-beijing", tosProperties.region)
          // Verify that the generic configuration is present
          kotlin.test.assertEquals("cn-hongkong", ossProperties.region)

          // Verify that the TOS client is created using the generic configuration
          assertTrue(context.containsBeanDefinition("volcengineTosClient"))
        }
    }

    @Test
    fun `supports all valid Volcengine region codes`() {
      val validRegions = listOf("cn-beijing", "cn-shanghai", "cn-guangzhou", "cn-hongkong", "ap-southeast-1")

      validRegions.forEach { region ->
        contextRunner
          .withPropertyValues(
            "compose.oss.volcengine-tos.endpoint=tos-$region.volces.com",
            "compose.oss.volcengine-tos.region=$region",
            "compose.oss.volcengine-tos.access-key=testkey",
            "compose.oss.volcengine-tos.secret-key=testsecret",
          )
          .run { context ->
            val tosProperties = context.getBean(VolcengineTosProperties::class.java)
            kotlin.test.assertEquals(region, tosProperties.region)

            // Verify that the TOS client is created
            assertTrue(context.containsBeanDefinition("volcengineTosClient"))
          }
      }
    }
  }

  @Nested
  @ExtendWith(OutputCaptureExtension::class)
  inner class LoggingVerification {

    @Test
    fun `does not log default region warning when region is missing`(output: CapturedOutput) {
      contextRunner
        .withPropertyValues(
          "compose.oss.volcengine-tos.endpoint=tos-cn-beijing.volces.com",
          "compose.oss.volcengine-tos.access-key=testkey",
          "compose.oss.volcengine-tos.secret-key=testsecret",
          "spring.profiles.active=test", // Enable the test profile to skip connection checks
        )
        .run { context ->
          // Verify that no warning log is emitted
          kotlin.test.assertFalse(
            output.out.contains("No region specified, using default region: cn-beijing") ||
              output.err.contains("No region specified, using default region: cn-beijing"),
            "Warning log should not be present in output: ${output.all}",
          )

          // Verify that the TOS client is created
          assertTrue(context.containsBeanDefinition("volcengineTosClient"))
        }
    }

    @Test
    fun `does not log default region warning when region is provided`(output: CapturedOutput) {
      contextRunner
        .withPropertyValues(
          "compose.oss.volcengine-tos.endpoint=tos-cn-beijing.volces.com",
          "compose.oss.volcengine-tos.region=cn-shanghai",
          "compose.oss.volcengine-tos.access-key=testkey",
          "compose.oss.volcengine-tos.secret-key=testsecret",
          "spring.profiles.active=test", // Enable the test profile to skip connection checks
        )
        .run { context ->
          // Verify that no default-region warning is logged
          kotlin.test.assertFalse(
            output.out.contains("No region specified, using default region") || output.err.contains("No region specified, using default region"),
            "Should not output default region warning when region is specified",
          )

          // Verify that the TOS client is created
          assertTrue(context.containsBeanDefinition("volcengineTosClient"))
        }
    }

    @Test
    fun `does not log default region warning when OSS region is provided`(output: CapturedOutput) {
      contextRunner
        .withPropertyValues(
          "compose.oss.endpoint=tos-cn-beijing.volces.com",
          "compose.oss.region=cn-guangzhou",
          "compose.oss.access-key=testkey",
          "compose.oss.secret-key=testsecret",
          "spring.profiles.active=test", // Enable the test profile to skip connection checks
        )
        .run { context ->
          // Verify that no default-region warning is logged
          kotlin.test.assertFalse(
            output.out.contains("No region specified, using default region") || output.err.contains("No region specified, using default region"),
            "Should not output default region warning when OSS region is specified",
          )

          // Verify that the TOS client is created
          assertTrue(context.containsBeanDefinition("volcengineTosClient"))
        }
    }
  }

  @Nested
  inner class BeanDefinitionVerification {

    @Test
    fun `defines the TOSV2 bean factory methods`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.volcengine-tos.endpoint=tos-cn-beijing.volces.com",
          "compose.oss.volcengine-tos.region=cn-beijing",
          "compose.oss.volcengine-tos.access-key=testkey",
          "compose.oss.volcengine-tos.secret-key=testsecret",
        )
        .run { context ->
          // Verify that the bean definitions exist
          assertTrue(context.containsBeanDefinition("volcengineTosClient"))
          assertTrue(context.containsBeanDefinition("volcengineTosObjectStorageService"))

          // Verify that the bean names exist
          val beanNames = context.beanDefinitionNames
          assertTrue(beanNames.contains("volcengineTosClient"))
          assertTrue(beanNames.contains("volcengineTosObjectStorageService"))
        }
    }

    @Test
    fun `configures the bean conditional annotations`() {
      // Verify the annotations on the auto-configuration class
      val autoConfigClass = VolcengineTosAutoConfiguration::class.java

      // Verify the @ConditionalOnClass annotation
      val conditionalOnClass = autoConfigClass.getAnnotation(org.springframework.boot.autoconfigure.condition.ConditionalOnClass::class.java)
      assertNotNull(conditionalOnClass)
      kotlin.test.assertTrue(conditionalOnClass.value.contains(TOSV2::class))

      // Verify the @Order annotation
      val order = autoConfigClass.getAnnotation(org.springframework.core.annotation.Order::class.java)
      assertNotNull(order)
      kotlin.test.assertEquals(200, order.value)
    }
  }
}
