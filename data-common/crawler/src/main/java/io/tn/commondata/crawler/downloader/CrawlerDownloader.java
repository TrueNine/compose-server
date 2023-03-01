package io.tn.commondata.crawler.downloader;

import io.tn.commondata.crawler.BasePageHandle;
import io.tn.commondata.crawler.selenium.NamedWrapperDriver;
import io.tn.core.dev.UnImplemented;

public sealed interface CrawlerDownloader
  permits io.tn.commondata.crawler.downloader.CrawlerDynamicDownloader, io.tn.commondata.crawler.downloader.CrawlerStaticDownloader {
  /**
   * 前处理
   * 对下载做预处理，如：动态请求中的get
   *
   * @param request 请求
   * @param driver  司机
   */
  void preProcess(io.tn.commondata.crawler.downloader.TargetRequest request, NamedWrapperDriver driver);

  /**
   * 委派指定其他下载方式
   *
   * @param request 请求
   */
  @UnImplemented
  default BasePageHandle delegateDownload(io.tn.commondata.crawler.downloader.TargetRequest request, NamedWrapperDriver driver) {
    throw new RuntimeException("新增接口没有复写委托代理器");
  }

  /**
   * 对请求进行后置处理
   *
   * @param request {@link io.tn.commondata.crawler.downloader.TargetRequest}
   */
  void postProcess(io.tn.commondata.crawler.downloader.TargetRequest request, NamedWrapperDriver driver);
}
