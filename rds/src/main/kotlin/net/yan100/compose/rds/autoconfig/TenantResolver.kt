package net.yan100.compose.rds.autoconfig

import net.yan100.compose.core.ctx.TenantContextHolder
import net.yan100.compose.core.lang.slf4j
import org.hibernate.cfg.AvailableSettings
import org.hibernate.context.spi.CurrentTenantIdentifierResolver
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
import org.springframework.stereotype.Component

@Component
class TenantResolver :
  CurrentTenantIdentifierResolver,
  HibernatePropertiesCustomizer {

  override fun resolveCurrentTenantIdentifier(): String {
    val id = TenantContextHolder.getCurrentTenantId()
    return if (null != id && net.yan100.compose.core.lang.Str.hasText(id)) id
    else net.yan100.compose.core.consts.DataBaseBasicFieldNames.Tenant.DEFAULT_TENANT_STR
  }

  override fun validateExistingCurrentSessions(): Boolean {
    return false
  }

  override fun customize(hibernateProperties: MutableMap<String, Any>) {
    hibernateProperties[AvailableSettings
      .MULTI_TENANT_IDENTIFIER_RESOLVER] = this
  }

  override fun isRoot(tenantId: String): Boolean {
    return tenantId == net.yan100.compose.core.consts.DataBaseBasicFieldNames.Tenant.ROOT_TENANT_STR
  }

  private val log = slf4j(this::class)

  init {
    log.debug("注册 hibernate 租户管理器")
  }
}
