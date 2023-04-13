package com.truenine.component.rds.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.entity.DbTestPresortTreeEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests
import org.testng.annotations.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Rollback
@SpringBootTest(classes = [RdsEntrance::class])
class TableRowDeleteRecordServiceImplTest :
  AbstractTransactionalTestNGSpringContextTests() {

  private val log = LogKt.getLog(this::class)

  @Autowired
  lateinit var delService: TableRowDeleteRecordServiceImplImpl

  @Autowired
  lateinit var mapper: ObjectMapper

  @Test
  @Rollback
  fun testSave() {
    val delData = DbTestPresortTreeEntity().apply {
      id = 2131241241
      title = "wwr"
    }
    val da = delService.save(delData)
    assertNotNull("不能保存为 null") { da }
    assertNotNull("不能保存为 null") { da?.entity }
    val sered = mapper.readValue(da!!.entity.entityJson, DbTestPresortTreeEntity::class.java)
    assertNotNull("不能保存为 null") { sered }
    log.info("a = {}, b = {}", delData, sered)
    assertEquals(mapper.writeValueAsString(delData), da.entity.entityJson, "不可转换为原先对象")
  }
}
