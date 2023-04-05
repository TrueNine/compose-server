package com.truenine.component.webapidoc.autoconfig;

import com.truenine.component.core.properties.SwaggerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@Import({
  OpenApiDocConfig.class,
  //OpenApiConfigController.class
})
@EnableConfigurationProperties(SwaggerProperties.class)
public class AutoConfigEntrance {
}
