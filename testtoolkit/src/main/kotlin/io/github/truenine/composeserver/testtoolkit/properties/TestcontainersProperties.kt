package io.github.truenine.composeserver.testtoolkit.properties

import org.springframework.boot.context.properties.ConfigurationProperties

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
 *         image: "postgres:17.4-alpine"
 *       redis:
 *         image: "redis:7.4.2-alpine3.21"
 *       minio:
 *         image: "minio/minio:RELEASE.2025-04-22T22-12-26Z"
 * ```
 *
 * @author TrueNine
 * @since 2025-07-19
 */
@ConfigurationProperties(prefix = "compose.testtoolkit.testcontainers")
data class TestcontainersProperties(
  /** PostgreSQL 配置 */
  val postgres: PostgresConfig = PostgresConfig(),

  /** Redis 配置 */
  val redis: RedisConfig = RedisConfig(),

  /** MinIO 配置 */
  val minio: MinioConfig = MinioConfig(),
)

/** # PostgreSQL 容器配置 */
data class PostgresConfig(
  /** PostgreSQL Docker 镜像 默认使用 postgres:17.4-alpine（当前 LTS 版本） */
  val image: String = "postgres:17.4-alpine",

  /** 默认数据库名称 */
  val databaseName: String = "testdb",

  /** 默认用户名 */
  val username: String = "test",

  /** 默认密码 */
  val password: String = "test",
)

/** # Redis 容器配置 */
data class RedisConfig(
  /** Redis Docker 镜像 默认使用 redis:7.4.2-alpine3.21（当前稳定版本） */
  val image: String = "redis:7.4.2-alpine3.21"
)

/** # MinIO 容器配置 */
data class MinioConfig(
  /** MinIO Docker 镜像 默认使用较新的稳定版本 */
  val image: String = "minio/minio:RELEASE.2025-04-22T22-12-26Z",

  /** 默认访问密钥 */
  val accessKey: String = "minioadmin",

  /** 默认秘密密钥 */
  val secretKey: String = "minioadmin",
)
