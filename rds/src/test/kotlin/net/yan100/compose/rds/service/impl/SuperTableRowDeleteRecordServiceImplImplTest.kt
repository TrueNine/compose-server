/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.rds.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import net.yan100.compose.rds.RdsEntrance
import net.yan100.compose.rds.entities.DbTestServiceEntity
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [RdsEntrance::class])
class SuperTableRowDeleteRecordServiceImplImplTest {
  @Autowired private lateinit var service: TableRowDeleteRecordServiceImpl

  @Autowired lateinit var mapper: ObjectMapper

  @Test
  fun testSaveAnyEntity() {
    val e =
      DbTestServiceEntity().apply {
        id = 131.toString()
        title = "测试"
      }
    val saved = service.saveAnyEntity(e)

    assertNotNull(saved)
    assertNotNull(saved.entity)
    assertNotNull(saved.entity!!.entityJson)

    val a = mapper.readValue(saved.entity!!.entityJson, DbTestServiceEntity::class.java)
    assertEquals(a.title, e.title)

    val abc = service.saveAnyEntity(null)
    assertNull(abc)
  }
}
