package io.github.truenine.composeserver.consts

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Validates database-related constants declared in {@link IDbNames}.
 */
class IDbNamesTest {

  @Test
  fun verifiesTenantConstants() {
    log.info("Verifying tenant constants")

    assertEquals(0L, IDbNames.Tenant.ROOT_TENANT)
    assertEquals("0", IDbNames.Tenant.ROOT_TENANT_STR)
    assertEquals(0L, IDbNames.Tenant.DEFAULT_TENANT)
    assertEquals("0", IDbNames.Tenant.DEFAULT_TENANT_STR)

    // Ensure tenant constants align with RBAC root ID
    assertEquals(IDbNames.Rbac.ROOT_ID, IDbNames.Tenant.ROOT_TENANT)
    assertEquals(IDbNames.Rbac.ROOT_ID, IDbNames.Tenant.DEFAULT_TENANT)

    log.info("ROOT_TENANT: {}", IDbNames.Tenant.ROOT_TENANT)
    log.info("ROOT_TENANT_STR: {}", IDbNames.Tenant.ROOT_TENANT_STR)
    log.info("DEFAULT_TENANT: {}", IDbNames.Tenant.DEFAULT_TENANT)
    log.info("DEFAULT_TENANT_STR: {}", IDbNames.Tenant.DEFAULT_TENANT_STR)
  }

  @Test
  fun verifiesRbacConstants() {
    log.info("Verifying RBAC constants")

    assertEquals(0L, IDbNames.Rbac.ROOT_ID)
    assertEquals("0", IDbNames.Rbac.ROOT_ID_STR)
    assertEquals(1L, IDbNames.Rbac.USER_ID)
    assertEquals("1", IDbNames.Rbac.USER_ID_STR)
    assertEquals(2L, IDbNames.Rbac.ADMIN_ID)
    assertEquals("2", IDbNames.Rbac.ADMIN_ID_STR)
    assertEquals(3L, IDbNames.Rbac.VIP_ID)
    assertEquals("3", IDbNames.Rbac.VIP_ID_STR)

    log.info("ROOT_ID: {}", IDbNames.Rbac.ROOT_ID)
    log.info("USER_ID: {}", IDbNames.Rbac.USER_ID)
    log.info("ADMIN_ID: {}", IDbNames.Rbac.ADMIN_ID)
    log.info("VIP_ID: {}", IDbNames.Rbac.VIP_ID)
  }

  @Test
  fun comparesRbacIdStringRepresentations() {
    log.info("Verifying RBAC ID string representations")

    assertEquals(IDbNames.Rbac.ROOT_ID.toString(), IDbNames.Rbac.ROOT_ID_STR)
    assertEquals(IDbNames.Rbac.USER_ID.toString(), IDbNames.Rbac.USER_ID_STR)
    assertEquals(IDbNames.Rbac.ADMIN_ID.toString(), IDbNames.Rbac.ADMIN_ID_STR)
    assertEquals(IDbNames.Rbac.VIP_ID.toString(), IDbNames.Rbac.VIP_ID_STR)

    log.info("All RBAC IDs match their string representations")
  }

}

private fun assertTrue(condition: Boolean, message: String) {
  kotlin.test.assertTrue(condition, message)
}
