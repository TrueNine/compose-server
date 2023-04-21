package com.truenine.component.datacommon.dataextract.common.crawler.selenium;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 硒池节点
 * 一个 webdriver 在池内的描述符
 *
 * @author TrueNine
 * @since 2022-10-28
 */
@Data
@AllArgsConstructor
public class NamedWrapperDriver {
  private WrappedDriver driver;
  private String id;
  private boolean using;

  public WrappedDriver driver() {
    return this.getDriver();
  }

  public String id() {
    return this.getId();
  }
}
