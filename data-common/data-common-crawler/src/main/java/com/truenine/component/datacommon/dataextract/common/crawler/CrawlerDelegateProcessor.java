package com.truenine.component.datacommon.dataextract.common.crawler;

import com.truenine.component.datacommon.dataextract.common.crawler.downloader.TaskInfo;
import org.jsoup.nodes.Document;

/**
 * @param <T> 代理类型
 * @author TrueNine
 * @since 2022-12-10
 */
public interface CrawlerDelegateProcessor<T> {
  default T delegateDocument(Document document) {
    throw new RuntimeException("未实现此代理");
  }

  default T delegateRawText(String text) {
    throw new RuntimeException("未实现此代理");
  }

  default T delegateStatic(TaskInfo info) {
    throw new RuntimeException("未实现此代理");
  }

  default T delegateDynamic(TaskInfo info) {
    throw new RuntimeException("未实现此代理");
  }
}
