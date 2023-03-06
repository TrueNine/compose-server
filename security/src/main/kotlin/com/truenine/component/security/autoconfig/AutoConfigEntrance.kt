package com.truenine.component.security.autoconfig

import org.springframework.context.annotation.Import
import org.springframework.security.access.prepost.PreAuthorize

/**
 * 自动配置入口
 *
 * @author TrueNine
 * @since 2022-10-28
 */
@PreAuthorize("hasPermission('A')")
@Import(DisableSecurityPolicyBean::class, CaptchaAutoConfiguration::class)
class AutoConfigEntrance

