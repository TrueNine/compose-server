package io.github.truenine.composeserver.testtoolkit.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

/**
 * # Testcontainers 配置属性
 *
 * 用于配置 Testcontainers 各个服务的 Docker 镜像版本。 提供默认的稳定版本，用户可通过配置文件自定义版本。
 *
 * ## 配置示例
 *
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
@ConfigurationProperties(prefix = "compose.testtoolkit.testcontainers")
data class TestcontainersProperties(
  /** 是否复用所有容器，默认为 true */
  var reuseAllContainers: Boolean = false,

  /** 容器默认停止超时时间（秒），默认 300 秒（5分钟） */
  var defaultStopTimeoutSeconds: Long = 300,

  /** 是否启用自动停止容器，默认启用以避免长期占用系统资源 */
  var enableAutoStop: Boolean = true,

  /** PostgreSQL 配置 */
  @NestedConfigurationProperty var postgres: PostgresConfig = PostgresConfig(),

  /** MySQL 配置 */
  @NestedConfigurationProperty var mysql: MysqlConfig = MysqlConfig(),

  /** Redis 配置 */
  @NestedConfigurationProperty var redis: RedisConfig = RedisConfig(),

  /** MinIO 配置 */
  @NestedConfigurationProperty var minio: MinioConfig = MinioConfig(),
)

/** # PostgreSQL 容器配置 */
data class PostgresConfig(
  /** 是否复用容器，默认复用 */
  var reuse: Boolean = true,
  /** PostgreSQL Docker 镜像 默认使用 postgres:17-alpine（当前 LTS 版本） */
  var image: String = "postgres:17.5-alpine3.22",

  /** 默认数据库名称 */
  var databaseName: String = "testdb",

  /** 默认用户名 */
  var username: String = "test",

  /** 默认密码 */
  var password: String = "test",

  /** 容器停止超时时间（秒），null 表示使用全局默认值 */
  var stopTimeoutSeconds: Long? = null,
)

/** # MySQL 容器配置 */
data class MysqlConfig(
  /** 是否复用容器，默认复用 */
  var reuse: Boolean = true,
  /** MySQL Docker 镜像 默认使用 mysql:8.0（当前 LTS 版本） */
  var image: String = "mysql:8.4.6-oraclelinux9",

  /** 默认数据库名称 */
  var databaseName: String = "testdb",

  /** 默认用户名 */
  var username: String = "test",

  /** 默认密码 */
  var password: String = "test",

  /** 默认根密码 */
  var rootPassword: String = "roottest",

  /** 容器停止超时时间（秒），null 表示使用全局默认值 */
  var stopTimeoutSeconds: Long? = null,
)

/** # Redis 容器配置 */
data class RedisConfig(
  /** 是否复用容器，默认复用 */
  var reuse: Boolean = true,
  /** Redis Docker 镜像 默认使用 redis:7-alpine（当前稳定版本） */
  var image: String = "redis:8.0.3-alpine3.21",

  /** 容器停止超时时间（秒），null 表示使用全局默认值 */
  var stopTimeoutSeconds: Long? = null,
)

/** # MinIO 容器配置 */
data class MinioConfig(
  /** 是否复用容器，默认复用 */
  var reuse: Boolean = true,
  /** MinIO Docker 镜像 默认使用较新的稳定版本 */
  var image: String = "minio/minio:RELEASE.2025-07-23T15-54-02Z",

  /** 默认访问密钥 */
  var accessKey: String = "minioadmin",

  /** 默认秘密密钥 */
  var secretKey: String = "minioadmin",

  /** 容器停止超时时间（秒），null 表示使用全局默认值 */
  var stopTimeoutSeconds: Long? = null,
)
