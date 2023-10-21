package net.yan100.compose.depend.mqtt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@ConfigurationProperties(prefix = "compose.mqtt-client")
public class SingleMqttProperties {
  /**
   * schema = tcp://
   */
  private String url;
  private Integer port = 1883;
  private String clientId = UUID.randomUUID().toString();
  private List<String> topics = new ArrayList<>();
  private String username = "";
  private String password = "";
  private Integer connectTimeoutSecond = 10;
  private Long completionTimeout = 1000L * 5L;
  private Integer qos = 0;
  private Integer keepAliveSecond = 10;

  public String getFullUrl() {
    return getUrl() + ":" + getPort();
  }
}
