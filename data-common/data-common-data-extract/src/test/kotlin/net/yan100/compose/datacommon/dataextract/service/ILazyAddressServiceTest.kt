package net.yan100.compose.datacommon.dataextract.service

import net.yan100.compose.datacommon.dataextract.DataExtractEntrance
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SpringBootTest(classes = [DataExtractEntrance::class])
class ILazyAddressServiceTest {
  @Autowired
  lateinit var lazys: ILazyAddressService

  @Test
  fun `test find children`() {
    val a = lazys.findAllChildrenByCode("4331")
    assertFalse(a.isEmpty())
    assertFailsWith<IllegalArgumentException> { lazys.findAllChildrenByCode("4") }

    assertFailsWith<IllegalArgumentException> { lazys.findAllChildrenByCode("433") }
    val b = lazys.findAllChildrenByCode("")
    println(b)
  }

  @Test
  fun `test lookupAllChildrenByCode`() {
    val a = lazys.lookupAllChildrenByCode("433127103", firstFind = { null }, deepCondition = { false }) { it.result }
    assertTrue(a.isNotEmpty())
    val b = lazys.lookupAllChildrenByCode("433127103221", firstFind = { null }, deepCondition = { false })
    {
      it.result
    }
    assertFalse(b.isNotEmpty())
  }
}
