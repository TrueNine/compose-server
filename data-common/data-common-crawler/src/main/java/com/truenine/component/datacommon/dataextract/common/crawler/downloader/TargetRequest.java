package com.truenine.component.datacommon.dataextract.common.crawler.downloader;


import com.truenine.component.core.http.Headers;
import com.truenine.component.core.http.Methods;
import com.truenine.component.core.http.UserAgents;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 目标要求
 *
 * @author TrueNine
 * @since 2022-10-28
 */
public class TargetRequest {

  private String method = Methods.GET;
  private Map<String, String> headers = new ConcurrentHashMap<>();
  private String url;

  public TargetRequest(String url) {
    this.url = url;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public void setDefaultEdgeUserAgent() {
    setUserAgent(UserAgents.EDGE_WIN_106.val());
  }

  public void setDefaultChromeUserAgent() {
    setUserAgent(UserAgents.CHROME_WIN_103.val());
  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setUserAgent(String ua) {
    addHeader(Headers.USER_AGENT, ua);
  }

  public void addHeader(String key, String val) {
    this.headers.put(key, val);
  }

  public void addHeaders(Map<String, String> headers) {
    this.headers.putAll(headers);
  }

  public Map<String, String> getHeaders() {
    return this.headers;
  }

  public void setHeaders(Map<String, String> headers) {
    this.headers = headers;
  }

  public String getHeader(String name) {
    return headers.get(name);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + ": URL = " + this.url;
  }
}
