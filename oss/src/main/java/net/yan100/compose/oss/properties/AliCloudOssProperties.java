package net.yan100.compose.oss.properties;

import lombok.Data;

@Data
public class AliCloudOssProperties {
    private Boolean enable = false;
    private String accessKey;
    private String endpoint;
    private String accessKeySecret;
    private String bucketName;
}
