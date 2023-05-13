package net.yan100.compose.core.ctx

import org.springframework.core.NamedInheritableThreadLocal

@Deprecated(message = "暂时不使用多租户设计")
object TenantContextHolder {
  @JvmStatic
  private val TENANT_ID =
    NamedInheritableThreadLocal<String>("TenantContextHolder::tenant_id")

  @JvmStatic
  fun setCurrentTenant(tenantId: String) {
    if (net.yan100.compose.core.lang.Str.hasText(tenantId)) {
      TENANT_ID.set(tenantId)
    } else {
      TENANT_ID.set(net.yan100.compose.core.consts.DataBaseBasicFieldNames.Tenant.DEFAULT_TENANT_STR)
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
