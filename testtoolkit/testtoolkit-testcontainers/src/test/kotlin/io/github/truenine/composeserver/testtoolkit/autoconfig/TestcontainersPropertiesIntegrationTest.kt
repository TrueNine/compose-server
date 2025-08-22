package io.github.truenine.composeserver.testtoolkit.autoconfig

import io.github.truenine.composeserver.testtoolkit.properties.TestcontainersProperties
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

/** # TestcontainersProperties 集成测试类 */
@SpringBootTest(
  classes = [TestConfigurationBean::class],
  properties =
    [
      "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
        "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
    ],
)
class TestcontainersPropertiesIntegrationTest {

  @Nested
  @SpringBootTest(
    classes = [TestConfigurationBean::class],
    properties =
      [
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
          "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
      ],
  )
  inner class DefaultConfiguration {

    @Autowired private lateinit var testcontainersProperties: TestcontainersProperties

    @Test
    fun should_inject_properties_with_defaults() {
      assertNotNull(testcontainersProperties)
      assertEquals("postgres:17.5-alpine3.22", testcontainersProperties.postgres.image)
      assertEquals("mysql:8.4.6-oraclelinux9", testcontainersProperties.mysql.image)
      assertEquals("redis:8.0.3-alpine3.21", testcontainersProperties.redis.image)
      assertEquals("minio/minio:RELEASE.2025-07-23T15-54-02Z", testcontainersProperties.minio.image)
    }
  }

  @Nested
  @SpringBootTest(
    classes = [TestConfigurationBean::class],
    properties =
      [
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
          "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
      ],
  )
  @TestPropertySource(
    properties =
      [
        "compose.testtoolkit.testcontainers.postgres.image=postgres:16-alpine",
        "compose.testtoolkit.testcontainers.postgres.database-name=customdb",
        "compose.testtoolkit.testcontainers.mysql.image=mysql:8.1",
        "compose.testtoolkit.testcontainers.mysql.database-name=mysqlcustomdb",
        "compose.testtoolkit.testcontainers.redis.image=redis:7.2-alpine",
        "compose.testtoolkit.testcontainers.minio.image=minio/minio:RELEASE.2024-01-01T00-00-00Z",
        "compose.testtoolkit.testcontainers.minio.access-key=customkey",
      ]
  )
  inner class CustomConfiguration {

    @Autowired private lateinit var testcontainersProperties: TestcontainersProperties

    @Test
    fun should_inject_properties_with_custom_values() {
      assertNotNull(testcontainersProperties)

      // 验证自定义 PostgreSQL 配置
      assertEquals("postgres:16-alpine", testcontainersProperties.postgres.image)
      assertEquals("customdb", testcontainersProperties.postgres.databaseName)
      assertEquals("test", testcontainersProperties.postgres.username) // 保持默认值

      // 验证自定义 MySQL 配置
      assertEquals("mysql:8.1", testcontainersProperties.mysql.image)
      assertEquals("mysqlcustomdb", testcontainersProperties.mysql.databaseName)
      assertEquals("test", testcontainersProperties.mysql.username) // 保持默认值
      assertEquals("test", testcontainersProperties.mysql.password) // 保持默认值
      assertEquals("roottest", testcontainersProperties.mysql.rootPassword) // 保持默认值

      // 验证自定义 Redis 配置
      assertEquals("redis:7.2-alpine", testcontainersProperties.redis.image)

      // 验证自定义 MinIO 配置
      assertEquals("minio/minio:RELEASE.2024-01-01T00-00-00Z", testcontainersProperties.minio.image)
      assertEquals("customkey", testcontainersProperties.minio.accessKey)
      assertEquals("minioadmin", testcontainersProperties.minio.secretKey) // 保持默认值
    }
  }
}
