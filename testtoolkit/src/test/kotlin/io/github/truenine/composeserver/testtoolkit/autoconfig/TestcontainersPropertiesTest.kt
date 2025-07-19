package io.github.truenine.composeserver.testtoolkit.autoconfig

import io.github.truenine.composeserver.testtoolkit.properties.MinioConfig
import io.github.truenine.composeserver.testtoolkit.properties.PostgresConfig
import io.github.truenine.composeserver.testtoolkit.properties.RedisConfig
import io.github.truenine.composeserver.testtoolkit.properties.TestcontainersProperties
import kotlin.test.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/** # TestcontainersProperties 测试类 */
class TestcontainersPropertiesTest {

  @Nested
  inner class DefaultValues {

    @Test
    fun should_have_correct_default_postgres_config() {
      val properties = TestcontainersProperties()

      assertEquals("postgres:17.4-alpine", properties.postgres.image)
      assertEquals("testdb", properties.postgres.databaseName)
      assertEquals("test", properties.postgres.username)
      assertEquals("test", properties.postgres.password)
    }

    @Test
    fun should_have_correct_default_redis_config() {
      val properties = TestcontainersProperties()

      assertEquals("redis:7.4.2-alpine3.21", properties.redis.image)
    }

    @Test
    fun should_have_correct_default_minio_config() {
      val properties = TestcontainersProperties()

      assertEquals("minio/minio:RELEASE.2025-04-22T22-12-26Z", properties.minio.image)
      assertEquals("minioadmin", properties.minio.accessKey)
      assertEquals("minioadmin", properties.minio.secretKey)
    }
  }

  @Nested
  inner class CustomValues {

    @Test
    fun should_accept_custom_postgres_config() {
      val customPostgres = PostgresConfig(image = "postgres:16-alpine", databaseName = "customdb", username = "customuser", password = "custompass")
      val properties = TestcontainersProperties(postgres = customPostgres)

      assertEquals("postgres:16-alpine", properties.postgres.image)
      assertEquals("customdb", properties.postgres.databaseName)
      assertEquals("customuser", properties.postgres.username)
      assertEquals("custompass", properties.postgres.password)
    }

    @Test
    fun should_accept_custom_redis_config() {
      val customRedis = RedisConfig(image = "redis:7.2-alpine")
      val properties = TestcontainersProperties(redis = customRedis)

      assertEquals("redis:7.2-alpine", properties.redis.image)
    }

    @Test
    fun should_accept_custom_minio_config() {
      val customMinio = MinioConfig(image = "minio/minio:RELEASE.2024-01-01T00-00-00Z", accessKey = "customkey", secretKey = "customsecret")
      val properties = TestcontainersProperties(minio = customMinio)

      assertEquals("minio/minio:RELEASE.2024-01-01T00-00-00Z", properties.minio.image)
      assertEquals("customkey", properties.minio.accessKey)
      assertEquals("customsecret", properties.minio.secretKey)
    }
  }
}
