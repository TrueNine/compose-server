package io.github.truenine.composeserver.testtoolkit.testcontainers

import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

/**
 * # 简单的新功能验证测试
 *
 * 用于验证重构后的基本功能是否正常工作，避免复杂的集成测试可能导致的问题。
 *
 * @author TrueNine
 * @since 2025-08-09
 */
@SpringBootTest
@Import(TestConfiguration::class)
@EnableAutoConfiguration(
  exclude = [DataSourceAutoConfiguration::class, DataSourceTransactionManagerAutoConfiguration::class, HibernateJpaAutoConfiguration::class]
)
class SimpleNewFeaturesVerificationTest : ICacheRedisContainer {

  @Test
  fun verify_extension_function_basic_functionality() =
    redis(resetToInitialState = false) { container ->
      // 验证容器实例不为空且正在运行
      assertNotNull(container, "容器不应为空")
      assertTrue(container.isRunning, "容器应该正在运行")

      // 验证端口映射存在
      val port = container.getMappedPort(6379)
      assertTrue(port > 0, "端口映射应该有效")

      Unit // 明确返回 Unit
    }

  @Test
  fun verify_lazy_variable_accessibility() {
    // 验证可以访问懒加载变量
    val lazyContainer = ICacheRedisContainer.redisContainerLazy
    assertNotNull(lazyContainer, "懒加载变量应该存在")

    // 验证懒加载变量的值是容器实例
    val container = lazyContainer.value
    assertNotNull(container, "懒加载的容器实例应该存在")
    assertTrue(container.isRunning, "懒加载的容器应该正在运行")
  }

  @Test
  fun verify_container_aggregation_basic_functionality() =
    containers(ICacheRedisContainer.redisContainerLazy) {
      // 验证可以在聚合上下文中访问容器
      val redisContainer = getRedisContainer()
      assertNotNull(redisContainer, "应该能够从上下文获取Redis容器")
      assertTrue(redisContainer!!.isRunning, "获取的容器应该正在运行")

      // 验证 getAllContainers 功能
      val allContainers = getAllContainers()
      assertTrue(allContainers.isNotEmpty(), "应该至少有一个容器")

      Unit // 明确返回 Unit
    }
}
