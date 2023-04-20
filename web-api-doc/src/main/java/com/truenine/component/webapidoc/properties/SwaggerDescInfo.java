package com.truenine.component.webapidoc.properties;

import lombok.Data;

@Data
public class SwaggerDescInfo {
  private String title = "TN-REST 项目";
  private String description = "由 Open-Api 3 提供的项目接口文档";
  private String version = "1.0";
  private String gitLocation = "https://github.com/TrueNine";
  private String author = "TrueNine";
  private String license = "不可商用私有协议";
  private String licenseUrl = "https://qq.com";
  private String group = "TrueNineGroup";
}
