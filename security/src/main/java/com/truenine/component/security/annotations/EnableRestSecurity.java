package com.truenine.component.security.annotations;

import com.truenine.component.security.autoconfig.FileKeysRepositoryAutoConfiguration;
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
  SecurityPolicyBean.class,
  FileKeysRepositoryAutoConfiguration.class
})
public @interface EnableRestSecurity {
  /**
   * @return 需要放行的匹配规则
   */
  String[] allowPatterns() default {};


  /**
   * @return 用户登录 url
   */
  String[] loginUrl() default {};

  /**
   * @return 退出登录 url
   */
  String[] logoutUrl() default {};

  /**
   * @return 允许 swagger api 放行
   */
  boolean allowSwagger() default false;

  /**
   * @return 允许 放行webjars
   */
  boolean allowWebJars() default true;
}
