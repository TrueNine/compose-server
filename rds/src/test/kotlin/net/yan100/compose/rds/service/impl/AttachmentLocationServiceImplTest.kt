package net.yan100.compose.rds.service.impl

import net.yan100.compose.rds.entity.AttachmentLocationEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
import kotlin.test.assertNotNull

@SpringBootTest
class AttachmentLocationServiceImplTest : AbstractTestNGSpringContextTests() {

  @Autowired
  lateinit var service: AttachmentLocationServiceImpl

  @Test
  fun testFindByBaseUrl() {
    service.save(AttachmentLocationEntity().apply {
      rn = true
      name = "测试API"
      baseUrl = "https://www.baidu.com/"
    })

    service.findByBaseUrl("https://www.baidu.com/").apply {
      assertNotNull(this)
    }
  }
}
