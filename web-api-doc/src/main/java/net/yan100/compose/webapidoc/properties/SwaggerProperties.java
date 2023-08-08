package net.yan100.compose.webapidoc.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "compose.web-api-doc.open-api")
public class SwaggerProperties {
  /**
   * 扫描的包
   */
  private String scanPackages = "com";
  /**
   * 扫描的路径
   */
  private List<String> scanUrlPatterns = new ArrayList<>(List.of("/**"));
  /**
   * 分组名称
   */
  private String group = "default";
  /**
   * 开启 jwt 请求头展示
   */
  private Boolean enableJwtHeader = false;

  /**
   * jwt 请求头信息
   */
  @NestedConfigurationProperty
  private JwtHeaderInfoProperties jwtHeaderInfo = new JwtHeaderInfoProperties();
  /**
   * 类型定义信息
   */
  @NestedConfigurationProperty
  private SwaggerDescInfo authorInfo = new SwaggerDescInfo();
}
