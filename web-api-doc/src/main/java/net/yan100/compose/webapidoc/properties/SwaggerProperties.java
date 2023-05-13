package net.yan100.compose.webapidoc.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = "compose.web-api-doc.open-api")
public class SwaggerProperties {
  /**
   * 扫描的包
   */
  private String packages = "com";
  /**
   * 分组名称
   */
  private String group = "default";
  /**
   * 类型定义信息
   */
  @NestedConfigurationProperty
  private SwaggerDescInfo authorInfoProperties = new SwaggerDescInfo();
}
