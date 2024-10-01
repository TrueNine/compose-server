package net.yan100.compose.kep.test.jpa.superdef.lateinit

import net.yan100.compose.testtookit.log
import kotlin.test.*

class LateinitTest {

  @Test
  fun `get reflected property`() {
    val la = Lateinit()

    val fields = Lateinit::class.java.declaredFields
    fields.forEach {
      it.isAccessible = true
      val e = it[la]
      log.info("field: {} value: {}", it.name, e)
      it.isAccessible = false
    }

    // 尝试获取方法并调用
    val methods = Lateinit::class.java.declaredMethods
    assertTrue { methods.isNotEmpty() }
    assertTrue { methods.any { it.name == "getName" } }
    assertTrue { methods.any { it.name == "setName" } }

    val getNameMethod = methods.find { it.name == "getName" }
    assertNotNull(getNameMethod)

    getNameMethod.isAccessible = true

    val r = getNameMethod.invoke(la)
    assertNull(r)
    log.info("getName result: {}", r)
    la.name = "hello"
    val r2 = getNameMethod.invoke(la)
    assertNotNull(r2)
    assertEquals("hello", r2)

    getNameMethod.isAccessible = false
  }
}
