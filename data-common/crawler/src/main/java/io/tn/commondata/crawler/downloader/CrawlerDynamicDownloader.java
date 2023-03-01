package io.tn.commondata.crawler.downloader;

import io.tn.commondata.crawler.BasePageHandle;
import io.tn.commondata.crawler.selenium.NamedWrapperDriver;

public non-sealed interface CrawlerDynamicDownloader extends CrawlerDownloader {


  @Override
  default BasePageHandle delegateDownload(io.tn.commondata.crawler.downloader.TargetRequest request, NamedWrapperDriver driver) {
    return dynamicDownload(request, driver);
  }

  BasePageHandle dynamicDownload(io.tn.commondata.crawler.downloader.TargetRequest request, NamedWrapperDriver driver);
}
