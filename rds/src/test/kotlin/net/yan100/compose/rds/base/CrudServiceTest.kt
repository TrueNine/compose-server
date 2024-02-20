/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.rds.base

import net.yan100.compose.core.id.Snowflake
import net.yan100.compose.core.lang.WGS84
import net.yan100.compose.rds.RdsEntrance
import net.yan100.compose.rds.entities.DbTestServiceEntity
import net.yan100.compose.rds.service.CrudServiceTester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import kotlin.test.*

@SpringBootTest(classes = [RdsEntrance::class])
class CrudServiceTest {

  @Autowired private lateinit var service: CrudServiceTester

  @Test
  fun testFindAll() {
    val a =
      service.save(
        DbTestServiceEntity().apply {
          title = "wad"
          center = WGS84(BigDecimal("1.3"), BigDecimal("2.44"))
        }
      )
    val all = service.findAll()
    assertContains(all.dataList, a, "a$a dataList${all.dataList}")
  }

  @Autowired lateinit var snowflake: Snowflake

  fun getEntity() =
    DbTestServiceEntity().apply {
      this.title = "dawda ${snowflake.nextStringId()}"
      this.center =
        WGS84(BigDecimal(snowflake.nextStringId()), BigDecimal(snowflake.nextStringId()))
    }

  @Test
  fun testSave() {
    val ab = service.save(getEntity())
    assertNotNull(ab)
    assertNotNull(ab.id)
  }

  fun getEs() = List(10) { getEntity() }

  @Test
  fun testFindAllByIdAndNotLogicDeleted() {
    val allId = service.saveAll(getEs()).map { it.id }
    val nots = service.findAllByIdAndNotLogicDeleted(allId)
    val all = service.findAll()
    println(all.dataList.map { it.ldf })
    println(nots)

    assertTrue(
      """
      allId$allId
      note${nots.dataList}
    """
        .trimIndent()
    ) {
      nots.dataList.map { it.id }.containsAll(allId)
    }

    val deld =
      service.saveAll(
        nots.dataList.map {
          it.ldf = true
          it
        }
      )
    service.findAllByIdAndNotLogicDeleted(deld.map { it.id }).let {
      assertFalse { it.dataList.map { it.ldf }.contains(true) }
    }
  }

  @Test
  fun testFindAllByNotLogicDeleted() {
    val all =
      service.saveAll(
        getEs().map {
          it.ldf = true
          it
        }
      )
    service.findAllByNotLogicDeleted().apply { assertFalse { this.dataList.containsAll(all) } }
  }

  @Test
  fun testFindById() {
    val ab = service.save(getEntity())
    assertNotNull(ab.id)
    val cd = service.findById(ab.id)
    assertEquals(ab, cd)
  }

  @Test
  fun testFindAllById() {
    val ess = service.saveAll(getEs())
    val finded = service.findAllById(ess.map { it.id })
    assertEquals(ess, finded)
    assertEquals(ess.size, finded.size)
  }

  @Test
  fun testFindByIdAndNotLogicDeleted() {
    val ess = service.save(getEntity())
    val t2 = service.logicDeleteById(ess.id)!!
    assertFailsWith<NullPointerException> { service.findByIdAndNotLogicDeleted(t2.id) }
  }

  @Test
  fun testFindByIdAndNotLogicDeletedOrNull() {
    val ess = service.save(getEntity())
    val t2 = service.logicDeleteById(ess.id)!!
    val t3 = service.findByIdAndNotLogicDeletedOrNull(t2.id)
    assertNull(t3)
  }

  @Test
  fun testFindLdfById() {
    val a = service.save(getEntity())
    val ab = service.findLdfById(a.id)
    assertFalse { ab }
  }

  @Test
  fun testCountAll() {
    val ess = service.saveAll(getEs())
    val countAll = service.countAll()
    assertTrue { countAll >= ess.size }
  }

  @Test
  @Transactional
  fun testCountAllByNotLogicDeleted() {
    val ess = service.saveAll(getEs())
    val allSize = service.countAll()
    ess.forEach { service.logicDeleteById(it.id) }
    val delSize = service.countAllByNotLogicDeleted()
    assertTrue("all$allSize del$delSize") { allSize > delSize }
  }

  @Test
  fun testExistsById() {
    val se = service.save(getEntity())
    assertTrue { service.existsById(se.id) }
  }

  @Test
  fun testSaveAll() {
    val eda = getEs()
    val all = service.saveAll(eda)
    assertEquals(eda.size, all.size)
    all.forEach { assertNotNull(it.id) }
  }

  @Test
  fun testDeleteById() {
    val aes = service.save(getEntity())
    service.deleteById(aes.id)
    assertNull(service.findById(aes.id))
  }

  @Test
  fun testDeleteAllById() {
    val all = service.saveAll(getEs())
    service.deleteAllById(all.map { it.id })

    service.findAllById(all.map { it.id }).let { assertTrue { it.isEmpty() } }
  }

  @Test
  fun testLogicDeleteById() {
    val ser = service.save(getEntity())
    val cc = service.logicDeleteById(ser.id)!!
    assertTrue { cc.ldf!! }
    val abc = service.findByIdAndNotLogicDeletedOrNull(ser.id)
    assertNull(abc)
  }
}
