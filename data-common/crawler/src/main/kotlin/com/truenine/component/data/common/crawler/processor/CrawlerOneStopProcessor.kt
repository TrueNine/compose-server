package com.truenine.component.data.common.crawler.processor

import com.truenine.component.data.common.crawler.CrawlerPageProcessor
import com.truenine.component.data.common.crawler.pipelines.CrawlerPipeline

/**
 * 一条龙服务，直接处理并且保存的一个复合接口
 *
 * @author TrueNine
 * @since 2022-11-21
 */
interface CrawlerOneStopProcessor : CrawlerPageProcessor, CrawlerPipeline
