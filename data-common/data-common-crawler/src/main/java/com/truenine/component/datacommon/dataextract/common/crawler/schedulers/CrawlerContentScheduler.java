package com.truenine.component.datacommon.dataextract.common.crawler.schedulers;

import com.truenine.component.datacommon.dataextract.common.crawler.downloader.PageContent;
import com.truenine.component.datacommon.dataextract.common.crawler.downloader.ThisTaskDetails;

import java.io.Closeable;

/**
 * 蜘蛛内容调度器
 *
 * @author TrueNine
 * @since 2022-10-28
 */
public interface CrawlerContentScheduler extends Closeable {
  /**
   * url是重复
   * url是否重复
   *
   * @param url     url
   * @param details {@link ThisTaskDetails}
   * @return boolean
   */
  boolean urlIsRepeated(String url, ThisTaskDetails details);

  /**
   * 内容是重复
   * 内容是否重复
   *
   * @param pageContent 页面内容
   * @param details     细节
   * @return boolean
   */

  boolean contentIsRepeated(PageContent pageContent, ThisTaskDetails details);
}
