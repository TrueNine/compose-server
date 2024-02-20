/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.core.ctx

import org.springframework.core.NamedInheritableThreadLocal

@Deprecated(message = "暂时不使用多租户设计")
object TenantContextHolder {
  @JvmStatic
  private val TENANT_ID = NamedInheritableThreadLocal<String>("TenantContextHolder::tenant_id")

  @JvmStatic
  fun setCurrentTenant(tenantId: String) {
    if (net.yan100.compose.core.lang.Str.hasText(tenantId)) {
      TENANT_ID.set(tenantId)
    } else {
      TENANT_ID.set(
        net.yan100.compose.core.consts.DataBaseBasicFieldNames.Tenant.DEFAULT_TENANT_STR
      )
    }
  }

  @JvmStatic
  fun getCurrentTenantId(): String? {
    return TENANT_ID.get()
  }

  @JvmStatic
  fun clear() {
    TENANT_ID.remove()
  }
}
