package com.truenine.component.core.api.http.mock;

/**
 * 一些收集的 userAgent 枚举
 * 使用 val() 方法进行调用
 *
 * @author TrueNine
 * @since 2022-10-28
 */
public enum UserAgents {
  /**
   * chrome windows 103
   */
  CHROME_WIN_103("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36"),
  /**
   * edge windows 106
   */
  EDGE_WIN_106("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36 Edg/106.0.1370.52");

  private final String ua;

  UserAgents(String ua) {
    this.ua = ua;
  }

  public String val() {
    return this.ua;
  }
}
