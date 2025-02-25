package net.yan100.compose.oss.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

private const val PREFIX = "compose.oss"

/**
 * oss属性
 *
 * @author TrueNine
 * @since 2022-10-28
 */
@ConfigurationProperties(prefix = PREFIX)
data class OssProperties(
  var type: Type = Type.FILE,

  /** ## 对外暴露的访问路径 */
  var exposeBaseUrl: String =
    "http://localhost:9999/not_set_oss_expose_base_url",

  /** ## minio相关配置 */
  @NestedConfigurationProperty var minio: MinioProperties = MinioProperties(),

  /** ## 阿里云相关配置 */
  @NestedConfigurationProperty
  var aliyun: AliCloudOssProperties = AliCloudOssProperties(),
) {

  /**
   * ## 类型
   *
   * @author TrueNine
   * @since 2022-10-28
   */
  enum class Type {
    /** 内置文件系统 */
    FILE,

    /** mysql 数据库 */
    MYSQL_DB,

    /** minio */
    MINIO,

    /** 阿里云 */
    ALI_CLOUD_OSS,

    /** 华为云 */
    HUAWEI_CLOUD,
  }
}
