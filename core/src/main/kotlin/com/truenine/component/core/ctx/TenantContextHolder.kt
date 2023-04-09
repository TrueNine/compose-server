package com.truenine.component.core.ctx

import com.truenine.component.core.consts.DataBaseBasicFieldNames
import com.truenine.component.core.lang.Str
import org.springframework.core.NamedInheritableThreadLocal

@Deprecated(message = "暂时不使用多租户设计")
object TenantContextHolder {
  @JvmStatic
  private val TENANT_ID =
    NamedInheritableThreadLocal<String>("TenantContextHolder::tenant_id")

  @JvmStatic
  fun setCurrentTenant(tenantId: String) {
    if (Str.hasText(tenantId)) {
      TENANT_ID.set(tenantId)
    } else {
      TENANT_ID.set(DataBaseBasicFieldNames.Tenant.DEFAULT_TENANT)
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
