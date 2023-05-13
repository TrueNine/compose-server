package net.yan100.compose.pay.autoconfig;

import net.yan100.compose.pay.api.WeChatApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class WebConfig {

  @Bean
  WebClient webClient() {
    return WebClient.builder()
      .build();
//      .baseUrl("https://api.weixin.qq.com")
  }

  @Bean
  WeChatApi toDoService() {
    HttpServiceProxyFactory httpServiceProxyFactory =
      HttpServiceProxyFactory.builder(WebClientAdapter.forClient(webClient()))
        .blockTimeout(Duration.of(30, ChronoUnit.SECONDS))
        .build();
    return httpServiceProxyFactory.createClient(WeChatApi.class);
  }

}
