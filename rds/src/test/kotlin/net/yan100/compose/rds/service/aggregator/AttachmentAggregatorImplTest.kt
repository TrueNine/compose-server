package net.yan100.compose.rds.service.aggregator


import net.yan100.compose.rds.RdsEntrance
import net.yan100.compose.rds.models.request.PostAttachmentRequestParam
import net.yan100.compose.rds.typing.AttachmentStorageTyping
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
      object : PostAttachmentRequestParam {
        override var baseUrl = "https://oss.aliyun.com/static"
        override var saveName = "adwd0juihjrthjrthrhrhrth"
        override var storageType = AttachmentStorageTyping.REMOTE
      }
    }!!.apply {
      assertNotNull(this.attachmentLocationId)
    }
  }

  @Test
  fun testGetFullUrl() {
    val mockFile = MockMultipartFile("abc", "测试文件".byteInputStream())
    ass.uploadAttachment(mockFile) {
      object : PostAttachmentRequestParam {
        override var baseUrl = "https://oss.aliyun.com/static"
        override var saveName = "adwd0juihjrthjrthrhrhrth"
        override var storageType = AttachmentStorageTyping.REMOTE
      }
    }!!
  }
}
