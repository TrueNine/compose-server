package net.yan100.compose.oss.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "compose.oss.ali-cloud")
public class AliCloudOssProperties {
  String accessKey;
  String endpoint;
  String accessKeySecret;
  String bucketName;
}
