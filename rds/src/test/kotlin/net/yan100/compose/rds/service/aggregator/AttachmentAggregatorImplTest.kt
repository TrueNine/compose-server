package net.yan100.compose.rds.service.aggregator


import net.yan100.compose.rds.RdsEntrance
import net.yan100.compose.rds.models.req.PostAttachmentReq
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
      PostAttachmentReq().apply {
        baseUrl = "https://oss.aliyun.com/static"
        saveName = "adwd0juihjrthjrthrhrhrth"
      }
    }!!.apply {
      assertNotNull(this.urlId)
    }
  }

  @Test
  fun testGetFullUrl() {
    val mockFile = MockMultipartFile("abc", "测试文件".byteInputStream())
    ass.uploadAttachment(mockFile) {
      PostAttachmentReq().apply {
        baseUrl = "https://oss.aliyun.com/static"
        saveName = "adwd0juihjrthjrthrhrhrth"
      }
    }!!
  }
}
