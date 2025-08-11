package io.github.truenine.composeserver.oss.volcengine.autoconfig

import com.volcengine.tos.TOSV2
import io.github.truenine.composeserver.oss.properties.OssProperties
import io.github.truenine.composeserver.oss.volcengine.properties.VolcengineTosProperties
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner

/**
 * 测试 Volcengine TOS 自动配置机制
 *
 * @author TrueNine
 * @since 2025-08-11
 */
class VolcengineTosAutoConfigurationTest {

  private val contextRunner = ApplicationContextRunner().withConfiguration(AutoConfigurations.of(VolcengineTosAutoConfiguration::class.java))

  @Nested
  inner class `自动配置启用条件` {

    @Test
    fun `当 TOSV2 类存在时应该启用自动配置`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.volcengine-tos.endpoint=tos-cn-beijing.volces.com",
          "compose.oss.volcengine-tos.region=cn-beijing",
          "compose.oss.volcengine-tos.access-key=testkey",
          "compose.oss.volcengine-tos.secret-key=testsecret",
        )
        .run { context ->
          // 验证配置属性被正确绑定
          val tosProperties = context.getBean(VolcengineTosProperties::class.java)
          kotlin.test.assertEquals("tos-cn-beijing.volces.com", tosProperties.endpoint)
          kotlin.test.assertEquals("cn-beijing", tosProperties.region)
          kotlin.test.assertEquals("testkey", tosProperties.accessKey)
          kotlin.test.assertEquals("testsecret", tosProperties.secretKey)

          // 验证 TOS 客户端被创建
          assertTrue(context.containsBeanDefinition("volcengineTosClient"))
          assertTrue(context.containsBeanDefinition("volcengineTosObjectStorageService"))
        }
    }

    @Test
    fun `当缺少必需配置时应该不创建客户端`() {
      contextRunner.run { context ->
        // 验证上下文启动失败（因为缺少必需配置）
        assertTrue(context.startupFailure != null)

        // 验证失败原因是缺少必需配置
        val failure = context.startupFailure
        assertTrue(
          failure?.message?.contains("endpoint") == true || failure?.message?.contains("region") == true || failure?.message?.contains("access") == true
        )
      }
    }
  }

  @Nested
  inner class `配置属性绑定` {

    @Test
    fun `应该正确绑定 Volcengine TOS 特定配置`() {
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
    fun `应该支持通用 OSS 配置作为后备`() {
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

          // 验证通用配置
          kotlin.test.assertEquals("oss.example.com", ossProperties.endpoint)
          kotlin.test.assertEquals("us-east-1", ossProperties.region)
          kotlin.test.assertEquals("osskey", ossProperties.accessKey)
          kotlin.test.assertEquals("osssecret", ossProperties.secretKey)

          // 验证 TOS 特定配置
          kotlin.test.assertEquals("tos.example.com", tosProperties.endpoint)
          kotlin.test.assertEquals("cn-beijing", tosProperties.region)
          kotlin.test.assertEquals("toskey", tosProperties.accessKey)
          kotlin.test.assertEquals("tossecret", tosProperties.secretKey)
        }
    }
  }

  @Nested
  inner class `向后兼容性` {

    @Test
    fun `应该忽略废弃的 provider 配置`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.provider=volcengine-tos", // 废弃的配置
          "compose.oss.volcengine-tos.endpoint=tos-cn-beijing.volces.com",
          "compose.oss.volcengine-tos.region=cn-beijing",
          "compose.oss.volcengine-tos.access-key=testkey",
          "compose.oss.volcengine-tos.secret-key=testsecret",
        )
        .run { context ->
          // 验证 provider 属性仍然可以读取（向后兼容）
          val ossProperties = context.getBean(OssProperties::class.java)
          kotlin.test.assertEquals("volcengine-tos", ossProperties.provider)

          // 验证 TOS 配置正确绑定
          val tosProperties = context.getBean(VolcengineTosProperties::class.java)
          kotlin.test.assertEquals("tos-cn-beijing.volces.com", tosProperties.endpoint)
          kotlin.test.assertEquals("cn-beijing", tosProperties.region)

          // 验证 TOS 客户端被创建
          assertTrue(context.containsBeanDefinition("volcengineTosClient"))
          assertTrue(context.containsBeanDefinition("volcengineTosObjectStorageService"))
        }
    }

    @Test
    fun `应该在没有 provider 配置时正常工作`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.volcengine-tos.endpoint=tos-cn-beijing.volces.com",
          "compose.oss.volcengine-tos.region=cn-beijing",
          "compose.oss.volcengine-tos.access-key=testkey",
          "compose.oss.volcengine-tos.secret-key=testsecret",
        )
        .run { context ->
          // 验证 provider 属性为 null
          val ossProperties = context.getBean(OssProperties::class.java)
          kotlin.test.assertNull(ossProperties.provider)

          // 验证 TOS 配置正确绑定
          val tosProperties = context.getBean(VolcengineTosProperties::class.java)
          kotlin.test.assertEquals("tos-cn-beijing.volces.com", tosProperties.endpoint)
          kotlin.test.assertEquals("cn-beijing", tosProperties.region)

          // 验证 TOS 客户端被创建
          assertTrue(context.containsBeanDefinition("volcengineTosClient"))
          assertTrue(context.containsBeanDefinition("volcengineTosObjectStorageService"))
        }
    }
  }

  @Nested
  inner class `Bean 定义验证` {

    @Test
    fun `应该定义 TOSV2 Bean 工厂方法`() {
      contextRunner
        .withPropertyValues(
          "compose.oss.volcengine-tos.endpoint=tos-cn-beijing.volces.com",
          "compose.oss.volcengine-tos.region=cn-beijing",
          "compose.oss.volcengine-tos.access-key=testkey",
          "compose.oss.volcengine-tos.secret-key=testsecret",
        )
        .run { context ->
          // 验证 Bean 定义存在
          assertTrue(context.containsBeanDefinition("volcengineTosClient"))
          assertTrue(context.containsBeanDefinition("volcengineTosObjectStorageService"))

          // 验证 Bean 名称存在
          val beanNames = context.beanDefinitionNames
          assertTrue(beanNames.contains("volcengineTosClient"))
          assertTrue(beanNames.contains("volcengineTosObjectStorageService"))
        }
    }

    @Test
    fun `应该正确配置 Bean 的条件注解`() {
      // 验证自动配置类的注解
      val autoConfigClass = VolcengineTosAutoConfiguration::class.java

      // 验证 @ConditionalOnClass 注解
      val conditionalOnClass = autoConfigClass.getAnnotation(org.springframework.boot.autoconfigure.condition.ConditionalOnClass::class.java)
      assertNotNull(conditionalOnClass)
      kotlin.test.assertTrue(conditionalOnClass.value.contains(TOSV2::class))

      // 验证 @Order 注解
      val order = autoConfigClass.getAnnotation(org.springframework.core.annotation.Order::class.java)
      assertNotNull(order)
      kotlin.test.assertEquals(200, order.value)
    }
  }
}
