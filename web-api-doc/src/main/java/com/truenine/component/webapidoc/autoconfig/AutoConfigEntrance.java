package com.truenine.component.webapidoc.autoconfig;

import com.truenine.component.webapidoc.config.OpenApiDocConfig;
import org.springframework.context.annotation.Import;

@Import({
  OpenApiDocConfig.class,
  //OpenApiConfigController.class
})
public class AutoConfigEntrance {
}
