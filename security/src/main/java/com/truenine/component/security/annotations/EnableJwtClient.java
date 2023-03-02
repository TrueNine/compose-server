package com.truenine.component.security.annotations;

import com.truenine.component.security.autoconfig.JwtClientBean;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启动 jwt 验证客户端
 *
 * @author TrueNine
 * @since 2022-12-14
 */
@Import(JwtClientBean.class)
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableJwtClient {
}
