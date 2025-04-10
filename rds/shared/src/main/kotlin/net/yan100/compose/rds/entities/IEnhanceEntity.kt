package net.yan100.compose.rds.entities

private const val DEPRECATED = "该接口或实体的方法不允许调用"

/**
 * ## 字节码增强实体
 *
 * 该实体用于预留增强过的实体类，该接口的方法无需调用
 */
@Suppress("ALL")
interface IEnhanceEntity {
  /** 为自身生成 snowflake id */
  @Throws(NotImplementedError::class)
  @Deprecated(DEPRECATED, level = DeprecationLevel.HIDDEN)
  fun ____compose_rds____self_generate_snowflake_id() {
    TODO(DEPRECATED)
  }

  /** ## 为自身生成 toString 的值 */
  @Throws(NotImplementedError::class)
  @Deprecated(DEPRECATED, level = DeprecationLevel.HIDDEN)
  fun ____compose_rds____all_field_toString(): String {
    TODO(DEPRECATED)
  }

  /** 为自身生成 bizCode */
  @Throws(NotImplementedError::class)
  @Deprecated(DEPRECATED, level = DeprecationLevel.HIDDEN)
  fun ____compose_rds____self_generate_biz_code() {
    TODO(DEPRECATED)
  }
}
