package io.tn.commondata.crawler.pipelines;

import io.tn.commondata.crawler.downloader.ThisTaskDetails;

/**
 * 存储器
 *
 * @author TrueNine
 * @since 2022-10-28
 */
public interface CrawlerPipeline {
  /**
   * 保存到一个存储实现
   *
   * @param data    数据
   * @param details {@link ThisTaskDetails}
   */
  void saveTo(ResultData data, ThisTaskDetails details);
}
