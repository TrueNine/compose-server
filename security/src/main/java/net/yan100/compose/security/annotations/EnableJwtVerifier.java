package net.yan100.compose.security.annotations;


import net.yan100.compose.security.autoconfig.FileKeysRepositoryAutoConfiguration;
import net.yan100.compose.security.autoconfig.JwtVerifierAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启动 jwt 验证客户端
 *
 * @author TrueNine
 * @since 2022-12-14
 */
@Import({
  JwtVerifierAutoConfiguration.class,
  FileKeysRepositoryAutoConfiguration.class
})
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableJwtVerifier {
}
