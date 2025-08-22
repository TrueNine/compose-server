package io.github.truenine.composeserver.testtoolkit.autoconfig

import io.github.truenine.composeserver.testtoolkit.log
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.MutablePropertySources

class TestEnvironmentApplicationListenerTest {

  private lateinit var listener: TestEnvironmentApplicationListener
  private lateinit var mockEvent: ApplicationEnvironmentPreparedEvent
  private lateinit var mockEnvironment: ConfigurableEnvironment
  private lateinit var mockPropertySources: MutablePropertySources

  @BeforeEach
  fun setUp() {
    log.trace("initializing test environment application listener test")

    mockPropertySources = mockk<MutablePropertySources>(relaxed = true)
    mockEnvironment = mockk<ConfigurableEnvironment> { every { propertySources } returns mockPropertySources }
    mockEvent = mockk<ApplicationEnvironmentPreparedEvent> { every { environment } returns mockEnvironment }

    listener = TestEnvironmentApplicationListener()
  }

  @Nested
  inner class OnApplicationEvent {

    @Test
    fun `启用状态 - 应当配置早期属性`() {
      log.trace("testing early property configuration when enabled")

      // 设置环境属性
      every { mockEnvironment.getProperty("compose.testtoolkit.enabled", Boolean::class.java, true) } returns true
      every { mockEnvironment.getProperty("compose.testtoolkit.disable-condition-evaluation-report", Boolean::class.java, true) } returns true
      every { mockEnvironment.getProperty("compose.testtoolkit.enable-virtual-threads", Boolean::class.java, true) } returns true
      every { mockEnvironment.getProperty("compose.testtoolkit.ansi-output-mode", String::class.java, "always") } returns "always"

      // 捕获添加的属性源
      var capturedPropertySource: MapPropertySource? = null
      every { mockPropertySources.addFirst(any<MapPropertySource>()) } answers { capturedPropertySource = firstArg() }

      listener.onApplicationEvent(mockEvent)

      // 验证属性源被添加
      verify { mockPropertySources.addFirst(any<MapPropertySource>()) }

      // 验证早期属性
      assertTrue(capturedPropertySource != null, "属性源不应为 null")
      capturedPropertySource?.also { propertySource ->
        // 验证属性源本身
        assertEquals("testToolkitEarlyProperties", propertySource.name, "属性源名称应详正确")
        assertNotNull(propertySource.source, "属性源数据应详不为 null")

        // 验证属性类型和值
        assertTrue(propertySource.getProperty("debug") is Boolean, "debug 属性应详为布尔类型")
        assertEquals(false, propertySource.getProperty("debug"), "debug 应详被设置为 false")

        assertTrue(propertySource.getProperty("spring.test.print-condition-evaluation-report") is Boolean, "条件评估报告属性应详为布尔类型")
        assertEquals(false, propertySource.getProperty("spring.test.print-condition-evaluation-report"), "条件评估报告应详被关闭")

        assertTrue(propertySource.getProperty("spring.main.log-startup-info") is Boolean, "启动信息日志属性应详为布尔类型")
        assertEquals(false, propertySource.getProperty("spring.main.log-startup-info"), "启动信息日志应详被关闭")

        assertTrue(propertySource.getProperty("spring.main.banner-mode") is String, "banner 模式属性应详为字符串类型")
        assertEquals("OFF", propertySource.getProperty("spring.main.banner-mode"), "banner 应详被关闭")

        assertTrue(propertySource.getProperty("spring.threads.virtual.enabled") is Boolean, "虚拟线程属性应详为布尔类型")
        assertEquals(true, propertySource.getProperty("spring.threads.virtual.enabled"), "虚拟线程应详被启用")

        assertTrue(propertySource.getProperty("spring.output.ansi.enabled") is String, "ANSI 输出属性应详为字符串类型")
        assertEquals("always", propertySource.getProperty("spring.output.ansi.enabled"), "颜色输出应详被启用")

        // 验证属性值的有效性
        assertTrue(propertySource.getProperty("spring.output.ansi.enabled") in listOf("always", "never", "detect"), "ANSI 输出模式应详为有效值")
      }

      log.debug("early property configuration verified when enabled")
    }

    @Test
    fun `禁用状态 - 不应当配置早期属性`() {
      log.trace("testing early property configuration when disabled")

      // 设置为禁用状态
      every { mockEnvironment.getProperty("compose.testtoolkit.enabled", Boolean::class.java, true) } returns false

      listener.onApplicationEvent(mockEvent)

      // 验证属性源未被添加
      verify(exactly = 0) { mockPropertySources.addFirst(any<MapPropertySource>()) }

      // 验证禁用时环境对象仍然有效
      assertNotNull(mockEnvironment, "环境对象不应为 null")
      assertNotNull(listener, "监听器对象不应为 null")

      log.debug("early property configuration verified when disabled")
    }

    @Test
    fun `部分配置 - 应当只配置启用的功能`() {
      log.trace("testing partial configuration")

      // 设置环境属性
      every { mockEnvironment.getProperty("compose.testtoolkit.enabled", Boolean::class.java, true) } returns true
      every { mockEnvironment.getProperty("compose.testtoolkit.disable-condition-evaluation-report", Boolean::class.java, true) } returns false
      every { mockEnvironment.getProperty("compose.testtoolkit.enable-virtual-threads", Boolean::class.java, true) } returns true
      every { mockEnvironment.getProperty("compose.testtoolkit.ansi-output-mode", String::class.java, "always") } returns "detect"

      // 捕获添加的属性源
      var capturedPropertySource: MapPropertySource? = null
      every { mockPropertySources.addFirst(any<MapPropertySource>()) } answers { capturedPropertySource = firstArg() }

      listener.onApplicationEvent(mockEvent)

      // 验证属性源被添加
      verify { mockPropertySources.addFirst(any<MapPropertySource>()) }

      // 验证只有虚拟线程和颜色输出被配置
      assertTrue(capturedPropertySource != null, "属性源不应为 null")
      capturedPropertySource?.also { propertySource ->
        // 验证被启用的属性
        assertTrue(propertySource.getProperty("spring.threads.virtual.enabled") is Boolean, "虚拟线程属性应详为布尔类型")
        assertEquals(true, propertySource.getProperty("spring.threads.virtual.enabled"), "虚拟线程应详被启用")

        assertTrue(propertySource.getProperty("spring.output.ansi.enabled") is String, "ANSI 输出属性应详为字符串类型")
        assertEquals("detect", propertySource.getProperty("spring.output.ansi.enabled"), "颜色输出应详被设置为 detect")

        // 验证未被设置的属性
        assertTrue(propertySource.getProperty("debug") == null, "debug 不应详被设置")
        assertTrue(propertySource.getProperty("spring.main.banner-mode") == null, "banner 不应详被设置")
        assertTrue(propertySource.getProperty("spring.test.print-condition-evaluation-report") == null, "条件评估报告不应详被设置")
        assertTrue(propertySource.getProperty("spring.main.log-startup-info") == null, "启动信息日志不应详被设置")

        // 验证属性值的有效性
        assertTrue(propertySource.getProperty("spring.output.ansi.enabled") in listOf("always", "never", "detect"), "ANSI 输出模式应详为有效值")
      }

      log.debug("partial configuration verified")
    }
  }
}
