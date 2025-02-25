package net.yan100.compose.data.extract.service.impl

import jakarta.annotation.Resource
import kotlin.test.*
import net.yan100.compose.core.exceptions.RemoteCallException
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@Ignore
@SpringBootTest
class LazyAddressServiceImplTest {
  lateinit var lazys: LazyAddressServiceImpl
    @Resource set

  private val testCode = "433127103101"

  @Test
  fun `find all provinces`() {
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
