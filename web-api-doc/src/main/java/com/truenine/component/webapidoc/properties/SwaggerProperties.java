package com.truenine.component.webapidoc.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "component.open-api")
public class SwaggerProperties {
  private String packages = "com";
  private String group = "default";
  private SwaggerDescInfo authorInfoProperties = new SwaggerDescInfo();
}
