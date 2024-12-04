/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.rds.crud.autoconfig

import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.core.hasText
import net.yan100.compose.core.holders.TenantContextHolder
import net.yan100.compose.core.slf4j
import org.hibernate.cfg.AvailableSettings
import org.hibernate.context.spi.CurrentTenantIdentifierResolver
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer

// @Component
@Deprecated(message = "暂时不使用多租户设计")
class TenantResolver : CurrentTenantIdentifierResolver<String>, HibernatePropertiesCustomizer {

  override fun resolveCurrentTenantIdentifier(): String {
    val id = TenantContextHolder.get()
    return if (id.hasText()) id else IDbNames.Tenant.DEFAULT_TENANT_STR
  }

  override fun validateExistingCurrentSessions(): Boolean {
    return false
  }

  override fun customize(hibernateProperties: MutableMap<String, Any>) {
    hibernateProperties[AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER] = this
  }

  override fun isRoot(tenantId: String): Boolean {
    return tenantId == IDbNames.Tenant.ROOT_TENANT_STR
  }

  private val log = slf4j(this::class)

  init {
    log.debug("注册 hibernate 租户管理器")
  }
}
