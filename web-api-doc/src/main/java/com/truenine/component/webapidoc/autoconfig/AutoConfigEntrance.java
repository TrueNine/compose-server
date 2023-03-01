package com.truenine.component.webapidoc.autoconfig;

import io.tn.webapidoc.config.OpenApiDocConfig;
import org.springframework.context.annotation.Import;

@Import({
        OpenApiDocConfig.class,
        //OpenApiConfigController.class
})
public class AutoConfigEntrance {
}
