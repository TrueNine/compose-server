/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.core.typing.http

import com.fasterxml.jackson.annotation.JsonValue
import net.yan100.compose.core.typing.IntTyping

/**
 * 错误信息枚举类
 *
 * @author TrueNine
 * @since 2022-10-28
 */
// TODO 支持 i18n
enum class HttpErrorStatus(val code: Int?, var message: String?, var alt: String?) : IntTyping {

  _400(400, "Bad Request", "用户错误"),
  _401(401, "Unauthorized", "请进行身份校验"),
  _402(402, "Payment Required", "需付款"),
  _403(403, "Forbidden", "无权限，禁止访问"),
  _404(404, "Not Found", "未找到"),
  _405(405, "Method Not Allowed", "禁止请求中指定的方法"),
  _406(406, "Not Acceptable", "无法接受"),
  _407(407, "Proxy Authentication Required", "需要进行代理身份验证"),
  _408(408, "Request Timeout", "请求超时"),
  _409(409, "Conflict", "与资源的当前状态冲突无法完成请求"),
  _410(410, "Gone", "资源在服务器上不再可用并且不知道转发地址"),
  _411(411, "Length Required", "没有定义的Content-Length 就无法处理请求"),
  _412(412, "Precondition Failed", "一个或多个请求头字段中给出的前提条件在服务器上测试时评估为假"),
  _413(413, "Request Entity Too Large", "请求实体大于服务器愿意或能够处理的"),
  _414(414, "Request URI Too Long", "Request-URI 比服务器愿意解释的长"),
  _415(415, "Unsupported Media Type", "不支持请求的媒体类型"),
  _416(416, "Requested Range Not Satisfiable", "无法提供请求的字节范围"),
  _417(417, "Expectation Failed", "服务器无法满足 Expect 请求标头中给出的期望"),
  _500(500, "Internal Server Error", "内部服务器错误"),
  _501(501, "Not Implemented", "服务器不支持当前请求所需要的功能"),
  _502(502, "Bad Gateway", "网关上游服务异常"),
  _503(503, "Service Unavailable", "由于超载或系统维护，服务器暂时的无法处理客户端的请求"),
  _504(504, "Gateway Timeout", "网关超时"),
  _505(505, "HTTP Version Not Supported", "服务器不支持或者拒绝 此 HTTP 版本"),
  UNKNOWN_ERROR(998, "Server Unknown Error", "发生了重大未知错误！目前错误原因未知，请尽快联系管理员或技术人员");

  @JsonValue override val value: Int = code!!

  companion object {
    @JvmStatic fun findVal(v: Int?) = valueOf(v)

    @Deprecated(message = "请改用 findVal")
    @JvmStatic
    fun valueOf(code: Int?): HttpErrorStatus {
      for (value in entries) {
        if (code == value.code) {
          return value
        }
      }
      return UNKNOWN_ERROR
    }
  }
}
