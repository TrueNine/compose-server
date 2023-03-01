package io.tn.commondata.crawler.downloader;

import io.tn.commondata.crawler.BasePageHandle;
import io.tn.commondata.crawler.StandardPageHandle;
import io.tn.commondata.crawler.annotations.PagePath;
import io.tn.commondata.crawler.selenium.NamedWrapperDriver;
import io.tn.core.api.http.MediaTypes;
import io.tn.core.lang.Str;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;

/**
 * 默认动态下载器
 *
 * @author TrueNine
 * @since 2022-11-01
 */
@PagePath
@Slf4j
public class StandardDynamicDownloader implements CrawlerDynamicDownloader {


  @Override
  public void preProcess(TargetRequest request, NamedWrapperDriver driver) {

  }

  @Override
  public BasePageHandle dynamicDownload(TargetRequest request, NamedWrapperDriver driver) {
    var page = new PageContent()
      .setUrl(request.getUrl())
      .setMimeType(MediaTypes.HTML);
    // 当拥有url时进行下载
    if (Str.hasText(request.getUrl())) {
      driver.getDriver().nativeDriver().get(request.getUrl());
    } else {
      log.info("执行了一个委派任务 request = {}, namedDriver = {}", request, driver);
    }

    var rawText = driver.driver().elementHtml(By.xpath("//html[1]"));
    page.setRawText(rawText);
    return new StandardPageHandle(page, driver);
  }

  @Override
  public void postProcess(TargetRequest request, NamedWrapperDriver driver) {

  }
}
