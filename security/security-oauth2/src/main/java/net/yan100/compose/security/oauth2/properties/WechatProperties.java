package net.yan100.compose.security.oauth2.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = "compose.security.oauth2.wechat")
public class WechatProperties {
  @NestedConfigurationProperty
  private WxpaProperties wxpa = new WxpaProperties();
}
