package net.yan100.compose.data.extract.api

import jakarta.annotation.Resource
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertNotNull

@Ignore
@SpringBootTest
class ICnNbsAddressApiTest {
  @Resource
  lateinit var remoteCall: ICnNbsAddressApi

  @Test
  fun testGetHomePage() {
    val homePage = remoteCall.homePage()
    assertNotNull(homePage)
  }

  @Test
  fun testGetCityPage() {
    val cityPage = remoteCall.getCityPage("43")
    assertNotNull(cityPage)
  }

  @Test
  fun testGetCountyPage() {
    val countyPage = remoteCall.getCountyPage("43", "31")
    assertNotNull(countyPage)
  }

  @Test
  fun testGetTownPage() {
    val townPage = remoteCall.getTownPage("43", "31", "27")
    assertNotNull(townPage)
  }

  @Test
  fun `test get village page`() {
    val villagePage = remoteCall.getVillagePage("43", "31", "27", "103")
    assertNotNull(villagePage)
  }
}
