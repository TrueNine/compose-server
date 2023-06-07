package net.yan100.compose.datacommon.dataextract.service.impl

import net.yan100.compose.core.exceptions.RemoteCallException
import net.yan100.compose.datacommon.dataextract.DataExtractEntrance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


@SpringBootTest(classes = [DataExtractEntrance::class])
class LazyAddressServiceImplTest : AbstractTestNGSpringContextTests() {

  @Autowired
  lateinit var lazys: LazyAddressServiceImpl


  private val testCode: Long = 433_127_103_101L

  @Test
  fun testFindAllProvinces() {
    val all = lazys.findAllProvinces()
    assertNotNull(all)
    assertTrue("all = $all") { all.isNotEmpty() }
  }

  @Test
  fun testFindAllCityByCode() {
    val all = lazys.findAllCityByCode(testCode)
    assertNotNull(all)
    assertTrue("all = $all") { all.isNotEmpty() }
  }

  @Test
  fun testFindAllCountyByCode() {
    val all = lazys.findAllCountyByCode(testCode)
    assertNotNull(all)
    assertTrue("当前 all = $all") { all.isNotEmpty() }
  }

  @Test
  fun testFindAllTownByCode() {
    val all = lazys.findAllTownByCode(testCode)
    assertNotNull(all)
    assertTrue("当前 all = $all") { all.isNotEmpty() }
  }

  @Test
  fun testFindAllVillageByCode() {
    val all = lazys.findAllVillageByCode(testCode)
    assertNotNull(all)
    assertTrue("all$all") { all.isNotEmpty() }

    assertFailsWith<RemoteCallException> {
      val nullables = lazys.findAllVillageByCode(430_000_000_000)
      assertNull(nullables)
    }
  }
}
