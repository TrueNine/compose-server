package net.yan100.compose.rds.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.rds.RdsEntrance
import net.yan100.compose.rds.entity.DbTestServiceEntity
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(classes = [RdsEntrance::class])
class TableRowDeleteRecordServiceImplImplTest {
  @Autowired
  private lateinit var service: TableRowDeleteRecordServiceImpl

  @Autowired
  lateinit var mapper: ObjectMapper

  @Test
  fun testSaveAnyEntity() {
    val e = DbTestServiceEntity().apply {
      id = 131.toString()
      title = "测试"
    }
    val saved = service.saveAnyEntity(e)

    assertNotNull(saved)
    assertNotNull(saved.entity)
    assertNotNull(saved.entity.entityJson)

    val a = mapper.readValue(saved.entity.entityJson, DbTestServiceEntity::class.java)
    assertEquals(a.title, e.title)

    val abc = service.saveAnyEntity(null)
    assertNull(abc)
  }
}
