package io.github.truenine.composeserver.holders

import io.github.truenine.composeserver.consts.IDbNames
import io.github.truenine.composeserver.hasText

@Deprecated(message = "暂时不使用多租户设计")
object TenantContextHolder : AbstractThreadLocalHolder<String>() {
  override fun set(value: String) {
    content = if (value.hasText()) value else IDbNames.Tenant.DEFAULT_TENANT_STR
  }
}
