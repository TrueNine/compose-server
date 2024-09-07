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
package net.yan100.compose.rds.service.aggregator

import net.yan100.compose.rds.RdsEntrance
import net.yan100.compose.rds.models.req.PostAttachmentDto
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import kotlin.test.assertNotNull

@SpringBootTest(classes = [RdsEntrance::class])
class IAttachmentAggregatorImplTest {

  @Autowired lateinit var ass: AttachmentAggregatorImpl

  @Test
  fun testUploadAttachment() {
    val mockFile = MockMultipartFile("abc", "测试文件".byteInputStream())
    ass
      .uploadAttachment(mockFile) {
        PostAttachmentDto().apply {
          baseUrl = "https://oss.aliyun.com"
          baseUri = "/static"
          saveName = "adwd0juihjrthjrthrhrhrth"
        }
      }!!
      .apply { assertNotNull(this.urlId) }
  }

  @Test
  fun testGetFullUrl() {
    val mockFile = MockMultipartFile("abc", "测试文件".byteInputStream())
    ass.uploadAttachment(mockFile) {
      PostAttachmentDto().apply {
        baseUrl = "https://oss.aliyun.com"
        baseUri = "/static"
        saveName = "adwd0juihjrthjrthrhrhrth"
      }
    }!!
  }
}
