package net.yan100.compose.oss.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * oss属性
 *
 * @author TrueNine
 * @since 2022-10-28
 */
@Data
@ConfigurationProperties(prefix = "compose.oss")
public class OssProperties {
  private Type type = Type.FILE;

  /**
   * ## 对外暴露的访问路径
   */
  private String exposeBaseUrl;

  /**
   * ## minio相关配置
   */
  @NestedConfigurationProperty
  private MinioProperties minio;

  /**
   * ## 阿里云相关配置
   */
  @NestedConfigurationProperty
  private AliCloudOssProperties aliyun;

  /**
   * ## 类型
   *
   * @author TrueNine
   * @since 2022-10-28
   */
  public enum Type {
    /**
     * 内置文件系统
     */
    FILE,
    /**
     * mysql 数据库
     */
    MYSQL_DB,
    /**
     * minio
     */
    MINIO,
    /**
     * 阿里云
     */
    ALI_CLOUD_OSS,
    /**
     * 华为云
     */
    HUAWEI_CLOUD
  }
}
