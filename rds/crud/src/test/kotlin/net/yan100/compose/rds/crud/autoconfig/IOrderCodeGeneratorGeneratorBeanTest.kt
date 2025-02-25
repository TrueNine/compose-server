package net.yan100.compose.rds.crud.autoconfig

import jakarta.annotation.Resource
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class IOrderCodeGeneratorGeneratorBeanTest {

  lateinit var bizId: BizCodeGeneratorBean
    @Resource set

  @Test
  fun testGenerate() {
    val id = bizId.generate(null, null)
    assertNotNull(id)
    println(id)
    assertTrue("生成的订单号不满足位数") { (id as? String)?.length == 21 }

    val batchIds = List(100) { bizId.generate(null, null) }

    println(batchIds.reduce { a, b -> "$a\n$b" })

    assertTrue("生成包含了重复ID") { batchIds.size == batchIds.toSet().size }
  }
}
