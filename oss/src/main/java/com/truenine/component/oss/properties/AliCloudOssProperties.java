package com.truenine.component.oss.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "component.oss.ali-cloud")
public class AliCloudOssProperties {
  String accessKey;
  String endpoint;
  String accessKeySecret;
  String bucketName;
}
