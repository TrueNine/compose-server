package itest.integrate.depend.jackson.config

import io.github.truenine.composeserver.depend.jackson.autoconfig.JacksonAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

/**
 * 时区测试专用配置类
 *
 * 放在单独的包中以避免与其他测试配置冲突
 */
@SpringBootApplication @Import(JacksonAutoConfiguration::class) class TimezoneTestConfiguration
