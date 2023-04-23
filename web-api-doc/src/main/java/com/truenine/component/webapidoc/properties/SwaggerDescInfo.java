package com.truenine.component.webapidoc.properties;

import lombok.Data;

@Data
public class SwaggerDescInfo {
  /**
   * 项目名称
   */
  private String title = "OPEN-API3 接口文档";
  /**
   * 项目描述
   */
  private String description = "由 Open-Api 3 提供的项目接口文档";
  /**
   * 项目版本
   */
  private String version = "1.0";
  /**
   * 网址
   */
  private String location = "https://github.com/TrueNine";
  /**
   * 作者
   */
  private String author = "百研科技";
  /**
   * 协议名称
   */
  private String license = "不可商用私有协议";
  /**
   * 协议地址
   */
  private String licenseUrl = "https://qq.com";
  /**
   * 分组名称
   */
  private String group = "广西映月文化传媒有限公司";
}
