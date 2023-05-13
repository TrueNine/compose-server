package net.yan100.compose.datacommon.dataextract.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test
import kotlin.test.assertNotNull

@SpringBootTest
class CnNbsAddressApiTest : AbstractTestNGSpringContextTests() {

  @Autowired
  lateinit var remoteCall: CnNbsAddressApi

  @Test
  fun testGetHomePage() {
    val homePage = remoteCall.homePage()
    assertNotNull(homePage)
  }

  @Test
  fun testGetCityPage() {
    val cityPage = remoteCall.getCityPage(43)
    assertNotNull(cityPage)
  }

  @Test
  fun testGetCountyPage() {
    val countyPage = remoteCall.getCountyPage(43, 31)
    assertNotNull(countyPage)
  }

  @Test
  fun testGetTownPage() {
    val townPage = remoteCall.getTownPage(43, 31, 27)
    assertNotNull(townPage)
  }

  @Test
  fun testGetVillagePage() {
    val villagePage = remoteCall.getVillagePage(43, 31, 27, 103)
    assertNotNull(villagePage)
  }
}
