package net.yan100.compose.meta.client

import net.yan100.compose.meta.types.Doc

/** 类、属性注释 */
data class ClientDoc(
  /** 注释内容 */
  override val value: String? = null,
  /** 注释头部 */
  override val title: String? = null,
  /** 注释描述 */
  override val description: String? = null,
  /** 参数描述 */
  override val parameters: Map<String, String> = emptyMap(),
  /** 是否已废弃 */
  override val deprecated: Boolean? = null,
  /** 废弃信息 */
  override val deprecatedMessage: String? = null,
  /** 从哪个版本开始 */
  override val since: String? = null,
  /** 作者 */
  override val author: String? = null,
) : Doc
