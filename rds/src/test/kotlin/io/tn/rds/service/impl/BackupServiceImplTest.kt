package io.tn.rds.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import io.tn.core.api.http.R
import io.tn.rds.RdsRunner
import jakarta.annotation.Resource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests
import org.testng.annotations.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(classes = [RdsRunner::class])
class BackupServiceImplTest : AbstractTransactionalTestNGSpringContextTests() {

  @Resource
  lateinit var backupService: BackupServiceImpl

  @Resource
  lateinit var mapper: ObjectMapper

  @Test
  fun testSave() {
    val nullObj = backupService.save(null)
    assertNull(nullObj, "数据保存了 null")

    val metaData = R.successfully()
    val serObj = backupService.save(metaData)
    assertNotNull(serObj, "没有保存数据")
    assertEquals(
      metaData,
      mapper.readValue(serObj.delSerObj, metaData.javaClass),
      "没有被正确反序列化"
    )
  }
}
