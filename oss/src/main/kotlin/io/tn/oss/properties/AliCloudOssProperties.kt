package io.tn.oss.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "center.oss.ali-cloud")
data class AliCloudOssProperties(
  var accessKey: String?,
  var endpoint: String?,
  var accessKeySecret: String?,
  var bucketName: String?
) {

}
