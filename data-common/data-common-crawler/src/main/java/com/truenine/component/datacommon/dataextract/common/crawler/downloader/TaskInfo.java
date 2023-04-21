package com.truenine.component.datacommon.dataextract.common.crawler.downloader;

import com.truenine.component.datacommon.dataextract.common.crawler.annotations.PagePath;
import com.truenine.component.datacommon.dataextract.common.crawler.selenium.NamedWrapperDriver;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * 任务信息
 *
 * @author TrueNine
 * @since 2022-11-03
 */
@Data
@Accessors(chain = true)
public class TaskInfo {
  private boolean dynamic = false;
  private TargetRequest request = new TargetRequest(null);
  private NamedWrapperDriver dynamicNode;
  private String routeTo = PagePath.ROOT_PATH;


  public TaskInfo addHeader(String key, String val) {
    request.addHeader(key, val);
    return this;
  }

  public TaskInfo setHeaders(Map<String, String> headers) {
    request.setHeaders(headers);
    return this;
  }

  public String getMethod() {
    return request.getMethod();
  }

  public TaskInfo setMethod(String method) {
    request.setMethod(method.toUpperCase());
    return this;
  }

  public String getUrl() {
    return this.request.getUrl();
  }

  public TaskInfo setUrl(String url) {
    this.request.setUrl(url);
    return this;
  }
}
