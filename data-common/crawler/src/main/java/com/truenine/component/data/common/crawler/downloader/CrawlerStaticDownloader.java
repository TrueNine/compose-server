package com.truenine.component.data.common.crawler.downloader;

import com.truenine.component.data.common.crawler.BasePageHandle;
import com.truenine.component.data.common.crawler.selenium.NamedWrapperDriver;

public non-sealed interface CrawlerStaticDownloader extends CrawlerDownloader {
  @Override
  default BasePageHandle delegateDownload(TargetRequest request, NamedWrapperDriver driver) {
    return staticDownload(request, driver);
  }

  BasePageHandle staticDownload(TargetRequest request, NamedWrapperDriver driver);
}

