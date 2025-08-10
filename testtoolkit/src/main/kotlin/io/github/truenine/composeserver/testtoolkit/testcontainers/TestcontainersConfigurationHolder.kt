package io.github.truenine.composeserver.testtoolkit.testcontainers

import io.github.truenine.composeserver.testtoolkit.log
import io.github.truenine.composeserver.testtoolkit.properties.TestcontainersProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

/**
 * # Testcontainers 配置持有者
 *
 * 静态访问 Spring 配置的工具类，用于在伴生对象中获取配置属性。 由于容器接口使用伴生对象，无法直接注入 Spring Bean，因此通过此工具类来获取配置。
 *
 * ## 容器重用配置
 *
 * 该类提供的配置属性控制着所有 TestContainers 的重用行为：
 * - **默认启用容器重用**：所有容器默认配置为可重用，以提高测试性能
 * - **全局重用开关**：`reuseAllContainers` 可以全局控制所有容器的重用行为
 * - **单独容器控制**：每个容器类型都有独立的重用配置选项
 *
 * ⚠️ **重要提醒**：容器重用会导致数据在测试间残留，请确保在测试中进行适当的数据清理。
 *
 * @author TrueNine
 * @since 2025-07-19
 */
@Component
class TestcontainersConfigurationHolder : ApplicationContextAware {

  companion object {
    private var applicationContext: ApplicationContext? = null

    /**
     * 获取 Testcontainers 配置属性
     *
     * @return TestcontainersProperties 或使用默认配置
     */
    fun getTestcontainersProperties(): TestcontainersProperties {
      return try {
        applicationContext?.getBean(TestcontainersProperties::class.java) ?: TestcontainersProperties()
      } catch (e: Exception) {
        // 如果无法获取配置，使用默认配置
        TestcontainersProperties()
      }
    }
  }

  override fun setApplicationContext(applicationContext: ApplicationContext) {
    log.trace("exposing testcontainers properties as bean")
    Companion.applicationContext = applicationContext
  }
}
