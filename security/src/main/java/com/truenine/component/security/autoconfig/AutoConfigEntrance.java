package com.truenine.component.security.autoconfig;

import org.springframework.context.annotation.Import;

/**
 * 自动配置入口
 *
 * @author TrueNine
 * @since 2022-10-28
 */
@Import({
  DisableSecurityPolicyBean.class,
  CaptchaBean.class
})
public class AutoConfigEntrance {
}
