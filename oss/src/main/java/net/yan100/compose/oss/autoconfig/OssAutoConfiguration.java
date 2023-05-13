package net.yan100.compose.oss.autoconfig;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import net.yan100.compose.oss.abstracts.Oss;
import net.yan100.compose.oss.minio.MinioClientWrapper;
import net.yan100.compose.oss.properties.AliCloudOssProperties;
import net.yan100.compose.oss.properties.MinioProperties;
import net.yan100.compose.oss.properties.OssProperties;
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
    log.debug("注册 minio = {}", p);
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
