package com.truenine.component.core.http

/**
 * 错误信息枚举类
 *
 * @author TrueNine
 * @since 2022-10-28
 */
enum class ErrMsg(val code: Int, var message: String, var alert: String) {
  /**
   * 400
   */
  _400(400, "Request Error", "用户错误"),

  /**
   * 401
   */
  _401(401, "Unauthorized", "请进行身份校验"),

  /**
   * 402
   */
  _402(402, "Payment Required", "需付款"),

  /**
   * 403
   */
  _403(403, "Forbidden", "无权限，禁止访问"),

  /**
   * 404
   */
  _404(404, "Not Found", "未找到"),

  /**
   * 405
   */
  _405(405, "Method Not Allowed", "禁止请求中指定的方法"),

  /**
   * 406
   */
  _406(406, "Not Acceptable", "无法接受"),

  /**
   * 407
   */
  _407(407, "Proxy Authentication Required", "需要进行代理身份验证"),

  /**
   * 408
   */
  _408(408, "Request Timeout", "请求超时"),

  /**
   * 409
   */
  _409(409, "Conflict", "与资源的当前状态冲突无法完成请求"),

  /**
   * 410
   */
  _410(410, "Gone", "资源在服务器上不再可用并且不知道转发地址"),

  /**
   * 411
   */
  _411(411, "Length Required", "没有定义的Content-Length 就无法处理请求"),

  /**
   * 412
   */
  _412(
    412,
    "Precondition Failed",
    "一个或多个请求头字段中给出的前提条件在服务器上测试时评估为假"
  ),

  /**
   * 413
   */
  _413(413, "Request Entity Too Large", "请求实体大于服务器愿意或能够处理的"),

  /**
   * 414
   */
  _414(414, "Request URI Too Long", "Request-URI 比服务器愿意解释的长"),

  /**
   * 415
   */
  _415(415, "Unsupported Media Type", "不支持请求的媒体类型"),

  /**
   * 416
   */
  _416(416, "Requested Range Not Satisfiable", "无法提供请求的字节范围"),

  /**
   * 417
   */
  _417(417, "Expectation Failed", "服务器无法满足 Expect 请求标头中给出的期望"),

  /**
   * 500
   */
  _500(500, "Internal Server Error", "内部服务器错误"),

  /**
   * 501
   */
  _501(501, "Not Implemented", "服务器不支持当前请求所需要的功能"),

  /**
   * 502
   */
  _502(502, "Bad Gateway", "网关上游服务异常"),

  /**
   * 503
   */
  _503(
    503,
    "Service Unavailable",
    "由于超载或系统维护，服务器暂时的无法处理客户端的请求"
  ),

  /**
   * 504
   */
  _504(504, "Gateway Timeout", "网关超时"),

  /**
   * 505
   */
  _505(505, "HTTP Version Not Supported", "服务器不支持或者拒绝 此 HTTP 版本"),

  /**
   * -1
   */
  UNKNOWN_ERROR(
    -1,
    "Server Unknown Error",
    "发生了重大未知错误！目前错误原因未知，请尽快联系管理员或技术人员"
  );

  companion object {
    fun valueOf(code: Int): ErrMsg {
      for (value in values()) {
        if (code == value.code) {
          return value
        }
      }
      return UNKNOWN_ERROR
    }
  }
}
