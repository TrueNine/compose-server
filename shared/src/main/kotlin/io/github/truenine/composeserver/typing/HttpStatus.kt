package io.github.truenine.composeserver.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.github.truenine.composeserver.IIntTyping

/**
 * HTTP 状态码枚举类
 *
 * @author TrueNine
 * @since 2022-10-28
 */
enum class HttpStatus(val code: Int, val message: String) : IIntTyping {
  _200(200, "OK"),
  _400(400, "Bad Request"),
  _401(401, "Unauthorized"),
  _402(402, "Payment Required"),
  _403(403, "Forbidden"),
  _404(404, "Not Found"),
  _405(405, "Method Not Allowed"),
  _406(406, "Not Acceptable"),
  _407(407, "Proxy Authentication Required"),
  _408(408, "Request Timeout"),
  _409(409, "Conflict"),
  _410(410, "Gone"),
  _411(411, "Length Required"),
  _412(412, "Precondition Failed"),
  _413(413, "Request Entity Too Large"),
  _414(414, "Request URI Too Long"),
  _415(415, "Unsupported Media Type"),
  _416(416, "Requested Range Not Satisfiable"),
  _417(417, "Expectation Failed"),
  _500(500, "Internal Server Error"),
  _501(501, "Not Implemented"),
  _502(502, "Bad Gateway"),
  _503(503, "Service Unavailable"),
  _504(504, "Gateway Timeout"),
  _505(505, "HTTP Version Not Supported"),
  UNKNOWN(9999, "Server Unknown Error");

  @JsonValue override val value: Int = code

  companion object {
    @JvmStatic
    operator fun get(statusCode: Int?): HttpStatus? {
      return entries.find { it.code == statusCode }
    }
  }
}
