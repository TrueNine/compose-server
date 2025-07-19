package io.github.truenine.composeserver.testtoolkit.testcontainers

import io.github.truenine.composeserver.testtoolkit.properties.TestcontainersProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

/**
 * # Testcontainers 配置持有者
 *
 * 静态访问 Spring 配置的工具类，用于在伴生对象中获取配置属性。 由于容器接口使用伴生对象，无法直接注入 Spring Bean， 因此通过此工具类来获取配置。
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
    Companion.applicationContext = applicationContext
  }
}
