package com.truenine.component.rds.service.impl

import com.truenine.component.core.http.MediaTypes
import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.models.req.PutAttachmentRequestParam
import jakarta.annotation.Resource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.util.*
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


@Rollback
@SpringBootTest(classes = [RdsEntrance::class])
class AttachmentServiceImplTest :
  AbstractTransactionalTestNGSpringContextTests() {

  @Resource
  lateinit var fileService: AttachmentServiceImpl
  private lateinit var testPutAttachmentRequestParam: PutAttachmentRequestParam

  @BeforeMethod
  fun init() {
    testPutAttachmentRequestParam =
      PutAttachmentRequestParam()
        .apply {
          this.size = 4400231
          this.dir = "base"
          this.mimeType = MediaTypes.WEBP.media()
          this.saveName = UUID.randomUUID().toString()
          this.url = "http://localhost:8080/test"
          this.fullName = "传说相册.webp"
        }
  }

  @Test
  fun testSaveFile() {
    val f = fileService.saveFile(testPutAttachmentRequestParam)
    assertNotNull(f, "没有保存文件")
  }


  @Test
  fun testListFiles() {
    fileService.saveFile(testPutAttachmentRequestParam)
    assertTrue("没有保存好file") {
      fileService.listFiles().dataList.isNotEmpty()
    }
  }

  companion object {
    private val log = LogKt.getLog(AttachmentServiceImplTest::class)
  }
}
