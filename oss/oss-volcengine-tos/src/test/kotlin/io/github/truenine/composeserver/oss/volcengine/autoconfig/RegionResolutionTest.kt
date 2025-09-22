package io.github.truenine.composeserver.oss.volcengine.autoconfig

import io.github.truenine.composeserver.oss.properties.OssProperties
import io.github.truenine.composeserver.oss.volcengine.properties.VolcengineTosProperties
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Simple region resolution logic tests
 */
class RegionResolutionTest {

  @Test
  fun `resolveRegion should use TOS region when specified`() {
    val config = VolcengineTosAutoConfiguration()
    val tosProps = VolcengineTosProperties().apply { region = "cn-shanghai" }
    val ossProps = OssProperties().apply { region = "cn-beijing" }

    // Use reflection to call private method
    val method = VolcengineTosAutoConfiguration::class.java.getDeclaredMethod(
      "resolveRegion",
      VolcengineTosProperties::class.java,
      OssProperties::class.java
    )
    method.isAccessible = true
    val result = method.invoke(config, tosProps, ossProps) as String

    assertEquals("cn-shanghai", result)
  }

  @Test
  fun `resolveRegion should use OSS region when TOS region is null`() {
    val config = VolcengineTosAutoConfiguration()
    val tosProps = VolcengineTosProperties().apply { region = null }
    val ossProps = OssProperties().apply { region = "cn-guangzhou" }

    // Use reflection to call private method
    val method = VolcengineTosAutoConfiguration::class.java.getDeclaredMethod(
      "resolveRegion",
      VolcengineTosProperties::class.java,
      OssProperties::class.java
    )
    method.isAccessible = true
    val result = method.invoke(config, tosProps, ossProps) as String

    assertEquals("cn-guangzhou", result)
  }

  @Test
  fun `resolveRegion should use default cn-beijing when both regions are null`() {
    val config = VolcengineTosAutoConfiguration()
    val tosProps = VolcengineTosProperties().apply { region = null }
    val ossProps = OssProperties().apply { region = null }

    // Use reflection to call private method
    val method = VolcengineTosAutoConfiguration::class.java.getDeclaredMethod(
      "resolveRegion",
      VolcengineTosProperties::class.java,
      OssProperties::class.java
    )
    method.isAccessible = true
    val result = method.invoke(config, tosProps, ossProps) as String

    assertEquals("cn-beijing", result)
  }
}
