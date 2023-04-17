package com.truenine.component.rds.service.aggregator

import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.models.SaveAttachmentModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
import kotlin.test.assertNotNull

@SpringBootTest(classes = [RdsEntrance::class])
class AttachmentAggregatorImplTest : AbstractTestNGSpringContextTests() {

  @Autowired
  lateinit var ass: AttachmentAggregatorImpl

  @Test
  fun testUploadAttachment() {
    val mockFile = MockMultipartFile("abc", "测试文件".byteInputStream())
    ass.uploadAttachment(mockFile) {
      SaveAttachmentModel().apply {
        baseUrl = "https://oss.aliyun.com/static"
        saveName = "adwd0juihjrthjrthrhrhrth"
      }
    }!!.apply {
      assertNotNull(this)
      assertNotNull(this.location)
      assertNotNull(this.attachmentLocationId)
      println(this.fullPath)
      println(this.fullPath)
    }
  }

  @Test
  fun testGetFullUrl() {
  }
}
