package com.truenine.component.data.common.crawler.downloader;

import com.truenine.component.data.common.crawler.BasePageHandle;
import com.truenine.component.data.common.crawler.selenium.NamedWrapperDriver;

public non-sealed interface CrawlerDynamicDownloader extends CrawlerDownloader {


  @Override
  default BasePageHandle delegateDownload(TargetRequest request, NamedWrapperDriver driver) {
    return dynamicDownload(request, driver);
  }

  BasePageHandle dynamicDownload(TargetRequest request, NamedWrapperDriver driver);
}
