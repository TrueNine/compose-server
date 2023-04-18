package com.truenine.component.datacommon.crawler.autoconfig

import com.truenine.component.datacommon.dataextract.common.crawler.selenium.ChromiumDriverOps.DriverType
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "component.crawler")
data class CrawlerProperties(
  var maxDriverCount: Int = 1,
  var maxSaveCount: Int = (maxDriverCount * 10),
  var driverType: DriverType = DriverType.CHROME,
)
