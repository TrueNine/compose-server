package com.truenine.component.data.common.crawler.autoconfig

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Import

@EnableConfigurationProperties(CrawlerProperties::class)
@Import(CrawlerLauncherAutoConfig::class)
class AutoConfigEntrance 