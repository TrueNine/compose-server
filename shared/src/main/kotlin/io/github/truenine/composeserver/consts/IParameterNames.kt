package io.github.truenine.composeserver.consts

/**
 * Request parameter name constants.
 *
 * @author TrueNine
 * @since 2022-12-26
 */
interface IParameterNames {
  companion object {
    const val X_TENANT_ID: String = "x_group_code"
    const val X_INTERNAL_TENANT_ID: String = "x_internal_tenant_id"
  }
}
