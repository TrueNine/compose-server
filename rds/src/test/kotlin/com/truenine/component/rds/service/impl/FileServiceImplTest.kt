package com.truenine.component.rds.service.impl

import com.truenine.component.core.api.http.MediaTypes
import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.dto.FsDto
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
class FileServiceImplTest : AbstractTransactionalTestNGSpringContextTests() {

  @Resource
  lateinit var fileService: FileServiceImpl
  private lateinit var testFsDto: FsDto

  @BeforeMethod
  fun init() {
    testFsDto =
      FsDto().apply {
        this.byteSize = 4400231
        this.dir = "base"
        this.mimeType = MediaTypes.WEBP.media()
        this.saveName = UUID.randomUUID().toString()
        this.url = "http://localhost:8080/test"
        this.fullName = "传说相册.webp"
      }
  }

  @Test
  fun testSaveFile() {
    val f = fileService.saveFile(testFsDto)
    assertNotNull(f, "没有保存文件")
  }


  @Test
  fun testListFiles() {
    fileService.saveFile(testFsDto)
    assertTrue("没有保存好file") {
      fileService.listFiles().contents.isNotEmpty()
    }
  }

  companion object {
    private val log = LogKt.getLog(FileServiceImplTest::class)
  }
}
