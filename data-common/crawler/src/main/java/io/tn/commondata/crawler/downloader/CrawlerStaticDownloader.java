package io.tn.commondata.crawler.downloader;

import io.tn.commondata.crawler.BasePageHandle;
import io.tn.commondata.crawler.selenium.NamedWrapperDriver;

public non-sealed interface CrawlerStaticDownloader extends CrawlerDownloader {
  @Override
  default BasePageHandle delegateDownload(TargetRequest request, NamedWrapperDriver driver) {
    return staticDownload(request, driver);
  }

  BasePageHandle staticDownload(TargetRequest request, NamedWrapperDriver driver);
}

