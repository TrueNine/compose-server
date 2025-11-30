package io.github.truenine.composeserver.testtoolkit.autoconfig

import io.github.truenine.composeserver.testtoolkit.properties.*
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

/** TestcontainersProperties test class. */
class TestcontainersPropertiesTest {

  @Test
  fun shouldHaveCorrectDefaultConfigs() {
    val properties = TestcontainersProperties()

    // postgres defaults
    assertEquals("postgres:17.6-alpine3.22", properties.postgres.image)
    assertEquals("testdb", properties.postgres.databaseName)
    assertEquals("test", properties.postgres.username)
    assertEquals("test", properties.postgres.password)

    // mysql defaults
    assertEquals("mysql:8.4.6-oraclelinux9", properties.mysql.image)
    assertEquals("testdb", properties.mysql.databaseName)
    assertEquals("test", properties.mysql.username)
    assertEquals("test", properties.mysql.password)
    assertEquals("roottest", properties.mysql.rootPassword)

    // redis defaults
    assertEquals("redis/redis-stack:7.2.0-v18", properties.redis.image)

    // minio defaults
    assertEquals("minio/minio:RELEASE.2025-09-07T16-13-09Z-cpuv1", properties.minio.image)
    assertEquals("minioadmin", properties.minio.accessKey)
    assertEquals("minioadmin", properties.minio.secretKey)
  }

  @Test
  fun shouldAcceptCustomConfigs() {
    val customPostgres = PostgresConfig(image = "postgres:16-alpine", databaseName = "customdb", username = "customuser", password = "custompass")
    val customMysql = MysqlConfig(image = "mysql:8.1", databaseName = "customdb", username = "customuser", password = "custompass", rootPassword = "customroot")
    val customRedis = RedisConfig(image = "redis:7.2-alpine")
    val customMinio = MinioConfig(image = "minio/minio:RELEASE.2024-01-01T00-00-00Z", accessKey = "customkey", secretKey = "customsecret")

    val properties = TestcontainersProperties(postgres = customPostgres, mysql = customMysql, redis = customRedis, minio = customMinio)

    assertEquals("postgres:16-alpine", properties.postgres.image)
    assertEquals("customdb", properties.postgres.databaseName)
    assertEquals("mysql:8.1", properties.mysql.image)
    assertEquals("redis:7.2-alpine", properties.redis.image)
    assertEquals("minio/minio:RELEASE.2024-01-01T00-00-00Z", properties.minio.image)
  }
}
