package com.truenine.component.oss.huaweicloud

import com.obs.services.ObsClient
import com.obs.services.model.ListBucketsRequest
import org.testng.annotations.Test


class ConnectorTest {

  @Test
  fun conn() {
    val endPoint = "https://obs.cn-south-1.myhuaweicloud.com"
    val ak = "PZQNQ0AWXIQ4PLEXDCDH"
    val sk = "LJAoiqOVCTUEm3cxp0KIdngnWBXb00XM9JbFlqNk"

    ObsClient(ak, sk, endPoint).use { obsClient ->
      // 列举桶
      obsClient.listBuckets(ListBucketsRequest().apply {
        isQueryLocation = true
      }).forEach(::println)
    }
  }
}
