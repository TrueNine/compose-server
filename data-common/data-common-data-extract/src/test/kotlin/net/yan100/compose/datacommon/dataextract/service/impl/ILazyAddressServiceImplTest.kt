package net.yan100.compose.datacommon.dataextract.service.impl

import net.yan100.compose.core.exceptions.RemoteCallException
import net.yan100.compose.datacommon.dataextract.DataExtractEntrance
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


@SpringBootTest(classes = [DataExtractEntrance::class])
class ILazyAddressServiceImplTest {

  @Autowired
  lateinit var lazys: ILazyAddressServiceImpl


  private val testCode = "433127103101"

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
  fun testFindAllCountyByCodeNotExists() {
    val all = lazys.findAllCountyByCode("330100000000")
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
      val nullables = lazys.findAllVillageByCode("430000000000")
      assertNull(nullables)
    }
  }
}
