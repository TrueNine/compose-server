package io.github.truenine.composeserver.testtoolkit.properties

import io.github.truenine.composeserver.testtoolkit.SpringBootConfigurationPropertiesPrefixes
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

/**
 * Testcontainers configuration properties.
 *
 * Configures Docker image versions and options for various Testcontainers-based services. Provides sensible defaults while allowing overrides via Spring
 * configuration.
 *
 * Example configuration:
 * ```yaml
 * compose:
 *   testtoolkit:
 *     testcontainers:
 *       postgres:
 *         image: "postgres:17-alpine"
 *       mysql:
 *         image: "mysql:8.0"
 *       redis:
 *         image: "redis:7-alpine"
 *       minio:
 *         image: "minio/minio:RELEASE.2025-07-23T15-54-02Z"
 * ```
 *
 * @author TrueNine
 * @since 2025-07-19
 */
@ConfigurationProperties(prefix = SpringBootConfigurationPropertiesPrefixes.TESTTOOLKIT_TESTCONTAINERS)
data class TestcontainersProperties(
  /** Whether to reuse all containers. */
  var reuseAllContainers: Boolean = false,

  /** Default container stop timeout in seconds (300 seconds = 5 minutes). */
  var defaultStopTimeoutSeconds: Long = 300,

  /** Whether to enable automatic container stop to avoid long-running containers. */
  var enableAutoStop: Boolean = true,

  /** PostgreSQL container configuration. */
  @NestedConfigurationProperty var postgres: PostgresConfig = PostgresConfig(),

  /** MySQL container configuration. */
  @NestedConfigurationProperty var mysql: MysqlConfig = MysqlConfig(),

  /** Redis container configuration. */
  @NestedConfigurationProperty var redis: RedisConfig = RedisConfig(),

  /** MinIO container configuration. */
  @NestedConfigurationProperty var minio: MinioConfig = MinioConfig(),
)

/** PostgreSQL container configuration. */
data class PostgresConfig(
  /** Whether to reuse the container. */
  var reuse: Boolean = true,
  /** PostgreSQL Docker image, default `postgres:17.6-alpine3.22` (current LTS). */
  var image: String = "postgres:17.6-alpine3.22",

  /** Default database name. */
  var databaseName: String = "testdb",

  /** Default username. */
  var username: String = "test",

  /** Default password. */
  var password: String = "test",

  /** Container stop timeout in seconds, or null to use the global default value. */
  var stopTimeoutSeconds: Long? = null,
)

/** MySQL container configuration. */
data class MysqlConfig(
  /** Whether to reuse the container. */
  var reuse: Boolean = true,
  /** MySQL Docker image, default `mysql:8.4.6-oraclelinux9` (current LTS). */
  var image: String = "mysql:8.4.6-oraclelinux9",

  /** Default database name. */
  var databaseName: String = "testdb",

  /** Default username. */
  var username: String = "test",

  /** Default password. */
  var password: String = "test",

  /** Default root password. */
  var rootPassword: String = "roottest",

  /** Container stop timeout in seconds, or null to use the global default value. */
  var stopTimeoutSeconds: Long? = null,
)

/** Redis container configuration. */
data class RedisConfig(
  /** Whether to reuse the container. */
  var reuse: Boolean = true,
  /** Redis Docker image, default `redis/redis-stack:7.2.0-v18` (current stable). */
  var image: String = "redis/redis-stack:7.2.0-v18",

  /** Container stop timeout in seconds, or null to use the global default value. */
  var stopTimeoutSeconds: Long? = null,
)

/** MinIO container configuration. */
data class MinioConfig(
  /** Whether to reuse the container. */
  var reuse: Boolean = true,
  /** MinIO Docker image, defaulting to a recent stable version. */
  var image: String = "minio/minio:RELEASE.2025-09-07T16-13-09Z-cpuv1",

  /** Default access key. */
  var accessKey: String = "minioadmin",

  /** Default secret key. */
  var secretKey: String = "minioadmin",

  /** Container stop timeout in seconds, or null to use the global default value. */
  var stopTimeoutSeconds: Long? = null,
)
