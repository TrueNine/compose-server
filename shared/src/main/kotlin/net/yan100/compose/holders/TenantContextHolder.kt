package net.yan100.compose.holders

import net.yan100.compose.consts.IDbNames
import net.yan100.compose.hasText

@Deprecated(message = "暂时不使用多租户设计")
object TenantContextHolder : AbstractThreadLocalHolder<String>() {
  override fun set(value: String) {
    content = if (value.hasText()) value else IDbNames.Tenant.DEFAULT_TENANT_STR
  }
}
