package com.truenine.component.rds.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.entity.DbTestBaseServiceEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(classes = [RdsEntrance::class])
class TableRowDeleteRecordServiceImplImplTest : AbstractTestNGSpringContextTests() {
  @Autowired
  private lateinit var service: TableRowDeleteRecordServiceImplImpl

  @Autowired
  lateinit var mapper: ObjectMapper

  @Test
  fun testSaveAnyEntity() {
    val e = DbTestBaseServiceEntity().apply {
      id = 131
      title = "测试"
    }
    val saved = service.saveAnyEntity(e)

    assertNotNull(saved)
    assertNotNull(saved.entity)
    assertNotNull(saved.entity.entityJson)

    val a = mapper.readValue(saved.entity.entityJson, DbTestBaseServiceEntity::class.java)
    assertEquals(a.title, e.title)

    val abc = service.saveAnyEntity(null)
    assertNull(abc)
  }
}
