package io.tn.rds.util

import io.tn.core.db.Bf
import io.tn.core.lang.Str

object TenantContextHolder {
  @JvmStatic
  private val TENANT_ID = ThreadLocal<String>()

  @JvmStatic
  fun setCurrentTenant(tenantId: String) {
    if (Str.hasText(tenantId)) {
      TENANT_ID.set(tenantId)
    } else {
      TENANT_ID.set(Bf.Tenant.DEFAULT_TENANT)
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
