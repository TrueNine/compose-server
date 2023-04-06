package com.truenine.component.rds.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.truenine.component.core.lang.LogKt
import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.dao.AddressDao
import com.truenine.component.rds.dao.DbTestPresortTreeDao
import com.truenine.component.rds.models.PointModel
import jakarta.annotation.Resource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.domain.AbstractPersistable_.id
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

  @Resource
  lateinit var delService: TableRowDeleteRecordServiceImpl

  @Resource
  lateinit var mapper: ObjectMapper

  @Test
  fun testSave() {
    val delData = DbTestPresortTreeDao().apply {
      id = "2131241241"
      title="wwr"
    }
    val da = delService.save(delData)
    assertNotNull("不能保存为 null") { da }
    assertNotNull("不能保存为 null") { da?.entity }
    val sered = mapper.readValue(da!!.entity.entityJson, DbTestPresortTreeDao::class.java)
    assertNotNull("不能保存为 null") { sered }
    log.info("a = {}, b = {}", delData, sered)
    assertEquals(delData, sered, "不可转换为原先对象")
  }
}
