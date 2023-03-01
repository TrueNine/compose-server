package com.truenine.component.oss.autoconfig;

import com.truenine.component.oss.abstracts.Oss;
import com.truenine.component.oss.minio.MinioClientWrapper;
import com.truenine.component.oss.properties.AliCloudOssProperties;
import com.truenine.component.oss.properties.MinioProperties;
import com.truenine.component.oss.properties.OssProperties;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties({
  OssProperties.class,
  MinioProperties.class,
  AliCloudOssProperties.class})
public class OssAutoConfiguration {

  @Bean(name = "minioClient")
  @ConditionalOnProperty(value = "oss.minio.enabled", havingValue = "true")
  MinioClient minioClient(MinioProperties p) {
    log.info("注册 minio = {}", p);
    return MinioClient.builder()
      .endpoint(p.getEndpointHost(), p.getEndpointPort(), false)
      .credentials(p.getAccessKey(), p.getSecretKey())
      .build();
  }

  @Bean(name = "objectStorageService")
  Oss oss(OssProperties p, ApplicationContext ctx) {
    return switch (p.getType()) {
      case MINIO -> new MinioClientWrapper(ctx.getBean(MinioClient.class));
      case FILE -> null;
      default -> null;
    };
  }
}
