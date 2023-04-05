package com.truenine.component.data.common.crawler.downloader;

import com.truenine.component.core.http.MediaTypes;
import com.truenine.component.core.lang.Str;
import com.truenine.component.data.common.crawler.BasePageHandle;
import com.truenine.component.data.common.crawler.StandardPageHandle;
import com.truenine.component.data.common.crawler.annotations.PagePath;
import com.truenine.component.data.common.crawler.selenium.NamedWrapperDriver;
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
      log.debug("执行了一个委派任务 request = {}, namedDriver = {}", request, driver);
    }

    var rawText = driver.driver().elementHtml(By.xpath("//html[1]"));
    page.setRawText(rawText);
    return new StandardPageHandle(page, driver);
  }

  @Override
  public void postProcess(TargetRequest request, NamedWrapperDriver driver) {

  }
}
