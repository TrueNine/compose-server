package net.yan100.compose.pay.autoconfig;

import net.yan100.compose.pay.properties.WeChatProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@Import(WeChatPayAutoConfiguration.class)
@EnableConfigurationProperties(WeChatProperties.class)
public class AutoConfigEntrance {


}
