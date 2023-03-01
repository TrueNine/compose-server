package io.tn.security.annotations;

import io.tn.security.jwt.JwtServerBean;
import io.tn.security.jwt.JwtServerBean;
import io.tn.security.jwt.JwtServerBean;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启动 jwt 验证客户端
 *
 * @author TrueNine
 * @since 2022-12-14
 */
@Import(JwtServerBean.class)
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableJwtServer {
}
