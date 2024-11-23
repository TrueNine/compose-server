/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.oss.huaweicloud

import com.obs.services.ObsClient
import com.obs.services.model.ListBucketsRequest
import kotlin.test.Ignore

class ConnectorTest {

  @Ignore
  fun conn() {
    val endPoint = "https://obs.cn-south-1.myhuaweicloud.com"
    val ak = "PZQNQ0AWXIQ4PLEXDCDH"
    val sk = "LJAoiqOVCTUEm3cxp0KIdngnWBXb00XM9JbFlqNk"
    ObsClient(ak, sk, endPoint).use { obsClient ->
      // 列举桶
      obsClient.listBuckets(ListBucketsRequest().apply { isQueryLocation = true }).forEach(::println)
    }
  }
}
