package com.truenine.component.oss.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * minio 配置项
 *
 * @author TrueNine
 * @since 2023-02-21
 */
@Data
@ConfigurationProperties(prefix = "center.oss.minio")
public class MinioProperties {
  Boolean enabled;
  String endpointHost = "localhost";
  int endpointPort = 9000;
  boolean secure = false;

  String accessKey;
  String secretKey;
}
