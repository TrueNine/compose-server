package net.yan100.compose.rds.service.aggregator


import net.yan100.compose.rds.RdsEntrance
import net.yan100.compose.rds.models.req.PostAttachmentReq
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import kotlin.test.assertNotNull

@SpringBootTest(classes = [RdsEntrance::class])
class IAttachmentAggregatorImplTest {

    @Autowired
    lateinit var ass: AttachmentAggregatorImpl

    @Test
    fun testUploadAttachment() {
        val mockFile = MockMultipartFile("abc", "测试文件".byteInputStream())
        ass.uploadAttachment(mockFile) {
            PostAttachmentReq().apply {
                baseUrl = "https://oss.aliyun.com"
                baseUri = "/static"
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
                baseUrl = "https://oss.aliyun.com"
                baseUri = "/static"
                saveName = "adwd0juihjrthjrthrhrhrth"
            }
        }!!
    }
}
