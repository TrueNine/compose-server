package net.yan100.compose.testtoolkit.autoconfig

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.yan100.compose.testtoolkit.log
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource
import org.springframework.core.env.MutablePropertySources
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestEnvironmentApplicationListenerTest {

  private lateinit var listener: TestEnvironmentApplicationListener
  private lateinit var mockEvent: ApplicationEnvironmentPreparedEvent
  private lateinit var mockEnvironment: ConfigurableEnvironment
  private lateinit var mockPropertySources: MutablePropertySources

  @BeforeEach
  fun setUp() {
    log.trace("initializing test environment application listener test")

    mockPropertySources = mockk<MutablePropertySources>(relaxed = true)
    mockEnvironment = mockk<ConfigurableEnvironment> {
      every { propertySources } returns mockPropertySources
    }
    mockEvent = mockk<ApplicationEnvironmentPreparedEvent> {
      every { environment } returns mockEnvironment
    }

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
      every { mockPropertySources.addFirst(any<MapPropertySource>()) } answers {
        capturedPropertySource = firstArg()
      }

      listener.onApplicationEvent(mockEvent)

      // 验证属性源被添加
      verify { mockPropertySources.addFirst(any<MapPropertySource>()) }

      // 验证早期属性
      capturedPropertySource?.also { propertySource ->
        assertEquals("testToolkitEarlyProperties", propertySource.name, "属性源名称应该正确")
        assertEquals(false, propertySource.getProperty("debug"), "debug 应该被设置为 false")
        assertEquals(false, propertySource.getProperty("spring.test.print-condition-evaluation-report"), "条件评估报告应该被关闭")
        assertEquals(false, propertySource.getProperty("spring.main.log-startup-info"), "启动信息日志应该被关闭")
        assertEquals("OFF", propertySource.getProperty("spring.main.banner-mode"), "banner 应该被关闭")
        assertEquals(true, propertySource.getProperty("spring.threads.virtual.enabled"), "虚拟线程应该被启用")
        assertEquals("always", propertySource.getProperty("spring.output.ansi.enabled"), "颜色输出应该被启用")
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
      every { mockPropertySources.addFirst(any<MapPropertySource>()) } answers {
        capturedPropertySource = firstArg()
      }

      listener.onApplicationEvent(mockEvent)

      // 验证属性源被添加
      verify { mockPropertySources.addFirst(any<MapPropertySource>()) }

      // 验证只有虚拟线程和颜色输出被配置
      capturedPropertySource?.also { propertySource ->
        assertEquals(true, propertySource.getProperty("spring.threads.virtual.enabled"), "虚拟线程应该被启用")
        assertEquals("detect", propertySource.getProperty("spring.output.ansi.enabled"), "颜色输出应该被设置为 detect")
        assertTrue(propertySource.getProperty("debug") == null, "debug 不应该被设置")
        assertTrue(propertySource.getProperty("spring.main.banner-mode") == null, "banner 不应该被设置")
      }

      log.debug("partial configuration verified")
    }
  }
} 