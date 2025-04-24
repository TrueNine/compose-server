package net.yan100.compose.testtookit.testcontainers

import jakarta.annotation.Resource
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
class StaticLaunchContainerTest : IDatabaseContainers {
  lateinit var environment: Environment @Resource set

  @Test
  fun `验证 PostgreSQL 容器成功启动`() {
    assertNotNull(postgresqlContainer, "PostgreSQL 容器应该存在")
    assertTrue(postgresqlContainer?.isRunning == true, "PostgreSQL 容器应该处于运行状态")
  }

  @Test
  fun `验证 Spring 环境中包含数据源配置`() {
    // 验证必要的数据源配置属性是否存在
    assertNotNull(environment.getProperty("spring.datasource.url"), "数据源 URL 应该存在")
    assertNotNull(environment.getProperty("spring.datasource.username"), "数据源用户名应该存在")
    assertNotNull(environment.getProperty("spring.datasource.password"), "数据源密码应该存在")
    assertNotNull(environment.getProperty("spring.datasource.driver-class-name"), "数据源驱动类名应该存在")

    // 验证 URL 是否指向 TestContainers 的 PostgreSQL
    val jdbcUrl = environment.getProperty("spring.datasource.url")
    assertTrue(jdbcUrl?.contains("jdbc:postgresql") == true, "JDBC URL 应该是 PostgreSQL 连接")
  }
}
