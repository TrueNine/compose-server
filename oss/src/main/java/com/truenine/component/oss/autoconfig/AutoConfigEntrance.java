package com.truenine.component.oss.autoconfig;


import com.truenine.component.oss.properties.AliCloudOssProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * @author TrueNine
 * @since 2022-10-28
 */
@ComponentScan("io.tn.oss.autoconfig")
@Import(AliCloudOssProperties.class)
public class AutoConfigEntrance {
}
