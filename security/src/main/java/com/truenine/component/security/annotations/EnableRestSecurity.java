package com.truenine.component.security.annotations;

import com.truenine.component.security.autoconfig.SecurityPolicyBean;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启安全管理器
 *
 * @author TrueNine
 * @since 2022-09-29
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({
  SecurityPolicyBean.class
})
public @interface EnableRestSecurity {

  /**
   * 用户登录 url
   *
   * @return {@link String[]}
   */
  String[] loginUrl();

  String[] logoutUrl();

  /**
   * 允许 swagger api 放行
   *
   * @return boolean
   */
  boolean allowSwagger() default false;


  /**
   * 允许 放行webjars
   *
   * @return boolean
   */
  boolean allowWebJars() default false;
}
