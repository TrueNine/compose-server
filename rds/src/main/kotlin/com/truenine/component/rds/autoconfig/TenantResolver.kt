package com.truenine.component.rds.autoconfig

import com.truenine.component.core.consts.DataBaseBasicFieldNames
import com.truenine.component.core.ctx.TenantContextHolder
import com.truenine.component.core.lang.LogKt
import com.truenine.component.core.lang.Str
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
    return if (null != id && Str.hasText(id)) id
    else DataBaseBasicFieldNames.Tenant.DEFAULT_TENANT_STR
  }

  override fun validateExistingCurrentSessions(): Boolean {
    return false
  }

  override fun customize(hibernateProperties: MutableMap<String, Any>) {
    hibernateProperties[AvailableSettings
      .MULTI_TENANT_IDENTIFIER_RESOLVER] = this
  }

  override fun isRoot(tenantId: String): Boolean {
    return tenantId == DataBaseBasicFieldNames.Tenant.ROOT_TENANT_STR
  }

  private val log = LogKt.getLog(this::class)

}
