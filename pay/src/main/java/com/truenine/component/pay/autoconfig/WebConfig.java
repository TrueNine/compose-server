package com.truenine.component.pay.autoconfig;

import com.truenine.component.pay.api.WeChatApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class WebConfig {

  @Bean
  WebClient webClient() {
    return WebClient.builder()
      .baseUrl("https://api.weixin.qq.com")
      .build();
  }

  @Bean
  WeChatApi toDoService() {
    HttpServiceProxyFactory httpServiceProxyFactory =
      HttpServiceProxyFactory.builder(WebClientAdapter.forClient(webClient()))
        .build();
    return httpServiceProxyFactory.createClient(WeChatApi.class);
  }

}
