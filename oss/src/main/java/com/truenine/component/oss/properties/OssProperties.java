package com.truenine.component.oss.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * oss属性
 *
 * @author TrueNine
 * @since 2022-10-28
 */
@Data
@ConfigurationProperties(prefix = "component.oss")
public class OssProperties {
  private Type type = Type.FILE;


  /**
   * 类型
   *
   * @author TrueNine
   * @date 2022-10-28
   */
  public enum Type {
    /**
     * 内置文件系统
     */
    FILE,
    /**
     * 存入mysql
     */
    MYSQL_DB,
    /**
     * minio
     */
    MINIO,
    /**
     * 阿里云
     */
    ALI_CLOUD_OSS
  }
}