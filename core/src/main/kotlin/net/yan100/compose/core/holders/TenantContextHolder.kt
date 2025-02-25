package net.yan100.compose.core.holders

import net.yan100.compose.core.consts.IDbNames
import net.yan100.compose.core.hasText

@Deprecated(message = "暂时不使用多租户设计")
object TenantContextHolder : AbstractThreadLocalHolder<String>() {
  override fun set(value: String) {
    content = if (value.hasText()) value else IDbNames.Tenant.DEFAULT_TENANT_STR
  }
}
