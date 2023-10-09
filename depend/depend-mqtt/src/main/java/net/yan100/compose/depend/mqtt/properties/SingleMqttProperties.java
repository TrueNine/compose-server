package net.yan100.compose.depend.mqtt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "compose.mqtt-client")
public class SingleMqttProperties {
  /**
   * schema = tcp://
   */
  private String url;
  private String clientId;
  private List<String> topics;
  private String username;
  private String password;
  private Integer connectTimeoutSecond = 10;
  private Long completionTimeout = 1000L * 5L;
  private Integer qos = 0;
  private Long keepAliveSecond = 10L;
}
