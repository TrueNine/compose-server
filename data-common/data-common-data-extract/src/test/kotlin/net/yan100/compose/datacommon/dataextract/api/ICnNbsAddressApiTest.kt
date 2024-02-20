/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.datacommon.dataextract.api

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertNotNull

@SpringBootTest
class ICnNbsAddressApiTest {
  @Autowired lateinit var remoteCall: ICnNbsAddressApi

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
  fun testGetVillagePage() {
    val villagePage = remoteCall.getVillagePage("43", "31", "27", "103")
    assertNotNull(villagePage)
  }
}
