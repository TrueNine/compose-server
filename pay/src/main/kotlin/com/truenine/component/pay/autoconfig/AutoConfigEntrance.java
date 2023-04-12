package com.truenine.component.pay.autoconfig;

import com.truenine.component.pay.properties.WeChatProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@Import(WeChatPayAutoConfiguration.class)
@EnableConfigurationProperties(WeChatProperties.class)
public class AutoConfigEntrance {


}
