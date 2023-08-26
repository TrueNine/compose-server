package net.yan100.compose.webservlet.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * spring web mvc http servlet 配置属性
 *
 * @author TrueNine
 * @since 2023-02-20
 */
@Data
@ConfigurationProperties(prefix = "compose.web-servlet")
public class ServletWebApplicationProperties {
  List<String> allowConverters = new ArrayList<>(Arrays.asList("getDocumentation",
    "swaggerResources",
    "openapiJson"));
  List<Class<?>> allowConverterClasses = new ArrayList<>(List.of(StringHttpMessageConverter.class));
}
