package net.yan100.compose.meta.client

/**
 * 根 root 解析API对象
 */
data class ClientApi(
  /**
   * 服务端点列表
   */
  val services: List<ClientService> = emptyList(),
  /**
   * 模型定义列表
   */
  val definitions: List<ClientType> = emptyList(),
)
