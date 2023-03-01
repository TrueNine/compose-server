package com.truenine.component.data.common.crawler.autoconfig

import com.truenine.component.core.lang.KtLogBridge
import com.truenine.component.data.common.crawler.CrawlerLauncher
import com.truenine.component.data.common.crawler.CrawlerPageProcessor
import com.truenine.component.data.common.crawler.downloader.CrawlerDynamicDownloader
import com.truenine.component.data.common.crawler.downloader.CrawlerStaticDownloader
import com.truenine.component.data.common.crawler.pipelines.CrawlerPipeline
import com.truenine.component.data.common.crawler.schedulers.CrawlerContentScheduler

import org.slf4j.Logger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class CrawlerLauncherAutoConfig {


  @Bean
  open fun crawlerLauncher(
    crawlerProperties: CrawlerProperties,
    processors: List<CrawlerPageProcessor>?,
    staticDownloads: List<CrawlerStaticDownloader>?,
    dynamicDownloads: List<CrawlerDynamicDownloader>?,
    contentSchedulers: List<CrawlerContentScheduler>?,
    pipelines: List<CrawlerPipeline>?
  ): CrawlerLauncher {
    log.info("创建 爬虫启动器")
    val launcher = CrawlerLauncher.create()
    crawlerProperties.also {
      launcher.browserCount(it.maxDriverCount)
      launcher.maxPipelineExecutePoolSize(it.maxSaveCount)
    }.also {
      launcher.browserType(it.driverType)
    }

    processors?.forEach { launcher.addPageProcessor(it) }
    staticDownloads?.forEach { launcher.addStaticDownloader(it) }
    dynamicDownloads?.forEach { launcher.addDynamicDownloader(it) }
    contentSchedulers?.forEach { launcher.addScheduler(it) }
    pipelines?.forEach { launcher.addPipeline(it) }
    launcher.submit()
    log.info("创建 爬虫 完毕，配置已提交 {}", launcher)
    return launcher
  }

  companion object {
    val log: Logger = KtLogBridge.getLog(CrawlerLauncherAutoConfig::class.java)
  }
}
