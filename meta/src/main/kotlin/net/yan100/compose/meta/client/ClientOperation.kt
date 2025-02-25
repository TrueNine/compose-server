package net.yan100.compose.meta.client

import net.yan100.compose.meta.types.Doc

/** 服务方法 */
data class ClientOperation(
  /** 方法名称 */
  val name: String,

  /** 重名时使用的 key */
  val key: String? = null,

  /** 应当被后置填充的 requestInfo */
  val requestInfo: ClientPostProcessApiOperationInfo? = null,

  /** 方法注释 */
  val doc: Doc? = null,

  /** 方法参数 */
  val params: List<ClientParameter> = emptyList(),

  /** 方法返回值 */
  val returnType: ClientType? = null,
)
