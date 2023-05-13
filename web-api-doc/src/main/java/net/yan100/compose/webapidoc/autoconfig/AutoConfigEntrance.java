package net.yan100.compose.webapidoc.autoconfig;

import net.yan100.compose.webapidoc.properties.SwaggerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@Import({
  OpenApiDocConfig.class,
  //OpenApiConfigController.class
})
@EnableConfigurationProperties(SwaggerProperties.class)
public class AutoConfigEntrance {
}
