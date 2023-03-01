package com.truenine.component.data.common.crawler.autoconfig

import com.truenine.component.data.common.crawler.selenium.ChromiumDriverOps.DriverType
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "center.crawler")
data class CrawlerProperties(
    var maxDriverCount: Int = 1,
    var maxSaveCount: Int = (maxDriverCount * 10),
    var driverType: DriverType = DriverType.CHROME,
)
