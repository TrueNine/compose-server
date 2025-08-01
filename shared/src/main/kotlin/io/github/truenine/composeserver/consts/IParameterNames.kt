package io.github.truenine.composeserver.consts

/**
 * 请求参数名常量
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
