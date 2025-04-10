package net.yan100.compose.rds.crud.autoconfig

import net.yan100.compose.consts.IDbNames
import net.yan100.compose.hasText
import net.yan100.compose.holders.TenantContextHolder
import net.yan100.compose.slf4j
import org.hibernate.cfg.AvailableSettings
import org.hibernate.context.spi.CurrentTenantIdentifierResolver
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer

// @Component
@Deprecated(message = "暂时不使用多租户设计")
class TenantResolver :
  CurrentTenantIdentifierResolver<String>, HibernatePropertiesCustomizer {

  override fun resolveCurrentTenantIdentifier(): String {
    val id = TenantContextHolder.get()
    return if (id.hasText()) id else IDbNames.Tenant.DEFAULT_TENANT_STR
  }

  override fun validateExistingCurrentSessions(): Boolean {
    return false
  }

  override fun customize(hibernateProperties: MutableMap<String, Any>) {
    hibernateProperties[AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER] =
      this
  }

  override fun isRoot(tenantId: String): Boolean {
    return tenantId == IDbNames.Tenant.ROOT_TENANT_STR
  }

  private val log = slf4j(this::class)

  init {
    log.debug("注册 hibernate 租户管理器")
  }
}
