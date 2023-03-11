package com.truenine.component.data.common.crawler.downloader;

import com.truenine.component.core.api.http.MediaTypes;
import com.truenine.component.core.api.http.Methods;
import com.truenine.component.core.lang.Str;
import com.truenine.component.data.common.crawler.StandardPageHandle;
import com.truenine.component.data.common.crawler.annotations.PagePath;
import com.truenine.component.data.common.crawler.selenium.NamedWrapperDriver;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

/**
 * 标准静态下载器
 *
 * @author TrueNine
 * @since 2022-11-02
 */
@PagePath
public class StandardStaticDownloader implements CrawlerStaticDownloader {

  private final Request.Builder requestBuilder = new Request.Builder();
  private final ThreadLocal<Request.Builder> PRE_BUILDER = new ThreadLocal<>();
  private final OkHttpClient CLIENT = new OkHttpClient();

  @Override
  public void preProcess(TargetRequest request, NamedWrapperDriver driver) {
    // 处理请求方法
    var method = Optional.ofNullable(request.getMethod())
      .orElse(Methods.GET).toUpperCase();
    // 请求头
    var headers = mapToOkhttp3Headers(request.getHeaders());

    this.PRE_BUILDER.set(
      requestBuilder.method(method, null)
        .url(request.getUrl())
        .headers(headers));
  }

  @Override
  public StandardPageHandle staticDownload(TargetRequest request, NamedWrapperDriver driver) {
    var builder = PRE_BUILDER.get();
    try (var response = CLIENT.newCall(builder
      .url(request.getUrl())
      .headers(mapToOkhttp3Headers(request.getHeaders()))
      .method(
        Str.hasText(request.getMethod())
          ? request.getMethod()
          : Methods.GET
        , null)
      .build()).execute()) {
      var rawText = response.body().string();
      var content = new PageContent()
        .setMimeType(
          MediaTypes.of(
            response.header(com.truenine.component.core.api.http.Headers.CONTENT_TYPE
            )))
        .setUrl(request.getUrl())
        .setRawText(rawText);
      return new StandardPageHandle(content, null);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void postProcess(TargetRequest request, NamedWrapperDriver driver) {
    PRE_BUILDER.remove();
  }

  private Headers mapToOkhttp3Headers(Map<String, String> headers) {
    var li = new ArrayList<String>();
    headers.forEach((k, v) -> {
      li.add(k);
      li.add(v);
    });
    return new Headers(li.toArray(String[]::new));
  }
}
