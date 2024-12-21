package net.yan100.compose.meta.client

import net.yan100.compose.meta.types.Doc

/**
 * 单个端点服务
 */
data class ClientService(
  /**
   * 所属类
   */
  val typeName: String,
  /**
   * 文档注释
   */
  val doc: Doc? = null,

  /**
   * 所以的方法
   */
  val operations: List<ClientOperation> = emptyList()
)
