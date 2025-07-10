package io.github.truenine.composeserver.testtoolkit.autoconfig

import io.github.truenine.composeserver.testtoolkit.SysLogger
import org.slf4j.LoggerFactory
import org.springframework.boot.Banner
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.env.MapPropertySource

/**
 * # 测试环境应用监听器
 *
 * 在应用环境准备阶段配置测试相关属性
 */
class TestEnvironmentApplicationListener : ApplicationListener<ApplicationEnvironmentPreparedEvent> {

  private val log: SysLogger = LoggerFactory.getLogger(TestEnvironmentApplicationListener::class.java)

  override fun onApplicationEvent(event: ApplicationEnvironmentPreparedEvent) {
    val environment = event.environment

    // 检查是否启用测试工具包
    val enabled = environment.getProperty("compose.testtoolkit.enabled", Boolean::class.java, true)
    if (!enabled) {
      log.debug("test toolkit disabled")
      return
    }

    log.trace("configuring early test properties")

    val testProperties = mutableMapOf<String, Any>()

    // 获取配置属性
    val disableConditionEvaluationReport = environment.getProperty("compose.testtoolkit.disable-condition-evaluation-report", Boolean::class.java, true)
    val enableVirtualThreads = environment.getProperty("compose.testtoolkit.enable-virtual-threads", Boolean::class.java, true)
    val ansiOutputMode = environment.getProperty("compose.testtoolkit.ansi-output-mode", String::class.java, "always")

    // 关闭条件评估报告和 banner
    if (disableConditionEvaluationReport) {
      testProperties["debug"] = false
      testProperties["spring.test.print-condition-evaluation-report"] = false
      testProperties["spring.main.log-startup-info"] = false
      testProperties["spring.main.banner-mode"] = Banner.Mode.OFF.name
      log.trace("disabled condition evaluation report and banner")
    }

    // 启用虚拟线程
    if (enableVirtualThreads) {
      testProperties["spring.threads.virtual.enabled"] = true
      log.trace("enabled virtual threads")
    }

    // 配置颜色输出
    testProperties["spring.output.ansi.enabled"] = ansiOutputMode
    log.trace("set ansi output: {}", ansiOutputMode)

    // 将属性添加到环境中
    if (testProperties.isNotEmpty()) {
      val propertySource = MapPropertySource("testToolkitEarlyProperties", testProperties)
      environment.propertySources.addFirst(propertySource)
      log.debug("added {} early properties", testProperties.size)
    }
  }
}
