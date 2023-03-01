package com.truenine.component.data.common.crawler.downloader;

import com.truenine.component.data.common.crawler.BasePageHandle;
import com.truenine.component.data.common.crawler.selenium.NamedWrapperDriver;
import io.tn.core.dev.UnImplemented;

public sealed interface CrawlerDownloader
  permits CrawlerDynamicDownloader, CrawlerStaticDownloader {
  /**
   * 前处理
   * 对下载做预处理，如：动态请求中的get
   *
   * @param request 请求
   * @param driver  司机
   */
  void preProcess(TargetRequest request, NamedWrapperDriver driver);

  /**
   * 委派指定其他下载方式
   *
   * @param request 请求
   */
  @UnImplemented
  default BasePageHandle delegateDownload(TargetRequest request, NamedWrapperDriver driver) {
    throw new RuntimeException("新增接口没有复写委托代理器");
  }

  /**
   * 对请求进行后置处理
   *
   * @param request {@link TargetRequest}
   */
  void postProcess(TargetRequest request, NamedWrapperDriver driver);
}
