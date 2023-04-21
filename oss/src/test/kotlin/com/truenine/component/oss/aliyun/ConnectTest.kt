package com.truenine.component.oss.aliyun

import com.aliyun.oss.OSSClientBuilder
import org.testng.annotations.Test


class ConnectTest {

  @Test
  fun connect() {
    val endpoint = "oss-cn-shenzhen.aliyuncs.com"
    val accessKeyId = "yourAccessKeyId"
    val accessKeySecret = "yourAccessKeySecret"
    val ossClient = OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret)
    ossClient.shutdown()
  }
}
