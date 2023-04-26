package com.truenine.component.rds.autoconfig

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest
class BizCodeGeneratorGeneratorBeanTest : AbstractTestNGSpringContextTests() {
  @Autowired
  lateinit var bizId: BizCodeGeneratorBean

  @Test
  fun testGenerate() {
    val id = bizId.generate(null, null)
    assertNotNull(id)
    println(id)
    assertTrue("生成的订单号不满足位数") {
      (id as? String)?.length == 21
    }

    val batchIds = List(100) {
      bizId.generate(null, null)
    }

    println(batchIds.reduce { a, b ->
      "$a\n$b"
    })

    assertTrue("生成包含了重复ID") {
      batchIds.size == batchIds.toSet().size
    }
  }
}
