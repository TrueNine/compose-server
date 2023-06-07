package net.yan100.compose.oss.properties;

import lombok.Data;

/**
 * minio 配置项
 *
 * @author TrueNine
 * @since 2023-02-21
 */
@Data
public class MinioProperties {
  private Boolean enable = false;
  private Boolean enableHttps = false;
  private String endpointHost = "localhost";
  private Integer endpointPort = 9000;
  private String accessKey;
  private String secretKey;
}
