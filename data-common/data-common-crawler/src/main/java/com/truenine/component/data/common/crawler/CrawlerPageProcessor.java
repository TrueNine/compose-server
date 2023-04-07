package com.truenine.component.data.common.crawler;

import com.truenine.component.data.common.crawler.downloader.PageContent;
import com.truenine.component.data.common.crawler.downloader.ThisTaskDetails;

/**
 * 静态页面处理器
 *
 * @author TrueNine
 * @since 2022-10-28
 */
public interface CrawlerPageProcessor {
  /**
   * 过程
   *
   * @param handle 控制器
   * @return {@link PageContent}
   */
  PageContent process(BasePageHandle handle);


  /**
   * 描述任务细节
   * 比如，爬虫的时间间隔
   *
   * @param builder 构建器
   * @return {@link ThisTaskDetails}
   */
  ThisTaskDetails describeTaskDetails(ThisTaskDetails.Builder builder);
}
