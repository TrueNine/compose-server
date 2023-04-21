package com.truenine.component.oss.aliyun

import com.aliyun.oss.OSSClientBuilder
import org.testng.annotations.Test


class ConnectTest {

  @Test
  fun connect() {
    val endpoint = "oss-cn-shenzhen.aliyuncs.com"
    val accessKeyId = "LTAI5tRUDCxxi9QusF5w6YMG"
    val accessKeySecret = "rmU482bQLrh7bkQbSXGin8U8h8DAXY"
    val ossClient = OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret)
    ossClient.listBuckets().forEach {
      it.extranetEndpoint
    }
    ossClient.shutdown()
  }
}
