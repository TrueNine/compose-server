package io.github.truenine.composeserver.consts

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Validates request parameter name constants defined in {@link IParameterNames}.
 */
class IParameterNamesTest {

  @Test
  fun verifiesTenantIdConstants() {
    log.info("Verifying tenant identifier constants")

    assertEquals("x_group_code", IParameterNames.X_TENANT_ID)
    assertEquals("x_internal_tenant_id", IParameterNames.X_INTERNAL_TENANT_ID)

    log.info("X_TENANT_ID: {}", IParameterNames.X_TENANT_ID)
    log.info("X_INTERNAL_TENANT_ID: {}", IParameterNames.X_INTERNAL_TENANT_ID)
  }

  @Test
  fun enforcesNamingConventions() {
    log.info("Verifying naming conventions")

    // Ensure parameters start with x_ (lowercase)
    assertTrue(IParameterNames.X_TENANT_ID.startsWith("x_"), "X_TENANT_ID should start with x_")
    assertTrue(IParameterNames.X_INTERNAL_TENANT_ID.startsWith("x_"), "X_INTERNAL_TENANT_ID should start with x_")

    // Ensure parameters use underscores for separation
    assertTrue(IParameterNames.X_TENANT_ID.contains("_"), "X_TENANT_ID should contain underscores")
    assertTrue(IParameterNames.X_INTERNAL_TENANT_ID.contains("_"), "X_INTERNAL_TENANT_ID should contain underscores")

    log.info("Naming conventions validated")
  }

  @Test
  fun ensuresParameterNamesAreUnique() {
    log.info("Verifying uniqueness of parameter names")

    val parameterNames = listOf(IParameterNames.X_TENANT_ID, IParameterNames.X_INTERNAL_TENANT_ID)

    val uniqueNames = parameterNames.toSet()

    assertEquals(parameterNames.size, uniqueNames.size, "All parameter names should be unique")

    log.info("Verified uniqueness for {} parameter names", parameterNames.size)
  }

}
