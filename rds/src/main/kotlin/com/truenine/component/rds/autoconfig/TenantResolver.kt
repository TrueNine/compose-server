package com.truenine.component.rds.autoconfig

import com.truenine.component.core.db.Bf
import com.truenine.component.core.lang.LogKt
import com.truenine.component.core.lang.Str
import com.truenine.component.rds.util.TenantContextHolder
import org.hibernate.cfg.AvailableSettings
import org.hibernate.context.spi.CurrentTenantIdentifierResolver
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
import org.springframework.stereotype.Component

@Component
open class TenantResolver :
  CurrentTenantIdentifierResolver,
  HibernatePropertiesCustomizer {

  override fun resolveCurrentTenantIdentifier(): String {
    val id = TenantContextHolder.getCurrentTenantId()
    return if (null != id && Str.hasText(id)) {
      id
    } else {
      Bf.Tenant.DEFAULT_TENANT
    }
  }

  override fun validateExistingCurrentSessions(): Boolean {
    return false
  }

  override fun customize(hibernateProperties: MutableMap<String, Any>) {
    hibernateProperties[AvailableSettings
      .MULTI_TENANT_IDENTIFIER_RESOLVER] = this
  }

  override fun isRoot(tenantId: String): Boolean {
    return tenantId == Bf.Tenant.ROOT_TENANT
  }

  companion object {
    @JvmStatic
    private val log = LogKt.getLog(TenantResolver::class)
  }
}
