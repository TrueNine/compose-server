package io.tn.commondata.crawler.processor

import io.tn.commondata.crawler.CrawlerPageProcessor
import io.tn.commondata.crawler.pipelines.CrawlerPipeline

/**
 * 一条龙服务，直接处理并且保存的一个复合接口
 *
 * @author TrueNine
 * @since 2022-11-21
 */
interface CrawlerOneStopProcessor : CrawlerPageProcessor, CrawlerPipeline
