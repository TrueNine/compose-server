package net.yan100.compose.meta.client

data class ClientPostProcessApiOperationInfo(
  /**
   * 访问此端点的全路径
   */
  val mappedUris: List<String> = emptyList(),

  /**
   * 访问此端点支持的方法
   */
  val supportedMethods: List<String>,

  /**
   * 所需填写的参数
   */
  val pathVariables: List<String>,

  /**
   * 接口接受的请求类型
   */
  val requestAcceptType: String,

  /**
   * 接口返回的响应类型
   */
  val responseContentType: String
)
