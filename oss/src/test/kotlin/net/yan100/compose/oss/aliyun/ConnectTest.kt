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
package net.yan100.compose.oss.aliyun

import com.aliyun.oss.OSSClientBuilder
import kotlin.test.Ignore

class ConnectTest {

  @Ignore
  fun connect() {
    val endpoint = "oss-cn-shenzhen.aliyuncs.com"
    val accessKeyId = "LTAI5tRUDCxxi9QusF5w6YMG"
    val accessKeySecret = "rmU482bQLrh7bkQbSXGin8U8h8DAXY"
    val ossClient = OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret)
    ossClient.listBuckets().forEach { it.extranetEndpoint }
    ossClient.shutdown()
  }
}
