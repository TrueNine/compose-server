package com.truenine.component.rds.base

import com.truenine.component.rds.RdsEntrance
import com.truenine.component.rds.dao.AddressDao
import com.truenine.component.rds.repo.AddressRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.transaction.annotation.Transactional
import org.testng.annotations.Test
import kotlin.test.assertEquals

@Rollback
@SpringBootTest(classes = [RdsEntrance::class])
open class PreSortTreeTest : AbstractTestNGSpringContextTests() {

  @Autowired
  lateinit var addressRepo: AddressRepo

  @Test
  @Transactional
  open fun testSaveChildren() {
    val addr = AddressDao().apply {
      this.name = "测试1"
      this.code = "123"
    }
    val addr2 = AddressDao().apply {
      this.name = "测试2"
      this.code = "123"
    }
    val addr3 = AddressDao().apply {
      this.name = "测试3"
      this.code = "123"
    }

    val af = addressRepo.saveChild(null, addr)
    val bf = addressRepo.saveChild(addr.id, addr2)

    val cf = addressRepo.saveChild(addr.id, addr3)

    val a = addressRepo.findByIdOrNull(af!!.id)
    val b = addressRepo.findByIdOrNull(bf!!.id)
    val c = addressRepo.findByIdOrNull(cf!!.id)

    val test = listOf(a, c, b).map {
      listOf(it!!.cln, it.crn)
    }.flatten()
    val bi = test.toSet()
    assertEquals(bi.size, test.size)

    val d = addressRepo.deleteChild(c!!)
  }

  @Test
  fun testSaveAllChildrenByParentId() {
    val data = listOf(AddressDao(), AddressDao(), AddressDao())
    addressRepo.saveAllChildrenByParentId("123", data)
  }
}
