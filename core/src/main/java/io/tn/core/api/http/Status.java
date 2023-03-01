package io.tn.core.api.http;

/**
 * http 响应状态
 *
 * @author TrueNine
 * @since 2022-10-28
 */
public enum Status {
  /**
   * 200
   */
  _200(true, 200, "ok", "请求成功"),

  /**
   * 400
   */
  _400(false, 400, "User Error", "用户错误"),
  /**
   * 401
   */
  _401(false, 401, "Unauthorized", "请进行身份校验"),
  /**
   * 402
   */
  _402(false, 402, "Payment Required", "需付款"),
  /**
   * 403
   */
  _403(false, 403, "Forbidden", "无权限，禁止访问"),
  /**
   * 404
   */
  _404(false, 404, "Not Found", "未找到"),
  /**
   * 405
   */
  _405(false, 405, "Method Not Allowed", "禁止请求中指定的方法"),
  /**
   * 406
   */
  _406(false, 406, "Not Acceptable", "无法接受"),
  /**
   * 407
   */
  _407(false, 407, "Proxy Authentication Required", "需要进行代理身份验证"),
  /**
   * 408
   */
  _408(false, 408, "Request Timeout", "请求超时"),
  /**
   * 409
   */
  _409(false, 409, "Conflict", "与资源的当前状态冲突无法完成请求"),
  /**
   * 410
   */
  _410(false, 410, "Gone", "资源在服务器上不再可用并且不知道转发地址"),
  /**
   * 411
   */
  _411(false, 411, "Length Required", "没有定义的Content-Length 就无法处理请求"),
  /**
   * 412
   */
  _412(false, 412, "Precondition Failed", "一个或多个请求头字段中给出的前提条件在服务器上测试时评估为假"),
  /**
   * 413
   */
  _413(false, 413, "Request Entity Too Large", "请求实体大于服务器愿意或能够处理的"),
  /**
   * 414
   */
  _414(false, 414, "Request URI Too Long", "Request-URI 比服务器愿意解释的长"),
  /**
   * 415
   */
  _415(false, 415, "Unsupported Media Type", "不支持请求的媒体类型"),
  /**
   * 416
   */
  _416(false, 416, "Requested Range Not Satisfiable", "无法提供请求的字节范围"),
  /**
   * 417
   */
  _417(false, 417, "Expectation Failed", "服务器无法满足 Expect 请求标头中给出的期望"),

  /**
   * 500
   */
  _500(false, 500, "Internal Server Error", "内部服务器错误"),
  /**
   * 501
   */
  _501(false, 501, "Not Implemented", "服务器不支持当前请求所需要的功能"),
  /**
   * 502
   */
  _502(false, 502, "Bad Gateway", "网关上游服务异常"),
  /**
   * 503
   */
  _503(false, 503, "Service Unavailable", "由于超载或系统维护，服务器暂时的无法处理客户端的请求"),
  /**
   * 504
   */
  _504(false, 504, "Gateway Timeout", "网关超时"),
  /**
   * 505
   */
  _505(false, 505, "HTTP Version Not Supported", "服务器不支持或者拒绝 此 HTTP 版本"),
  /**
   * 509
   */
  _509(false, 509, "Server Unknown Error", "发生了重大错误，目前错误类型未知，请尽快联系管理员或开发者");


  private final boolean ok;
  private final int code;
  private String message;
  private String alert;

  Status(boolean ok, int code, String message, String alert) {
    this.ok = ok;
    this.code = code;
    this.message = message;
    this.alert = alert;
  }

  public static Status valueOf(int code) {
    for (Status value : Status.values()) {
      if (code == value.getCode()) {
        return value;
      }
    }
    return _509;
  }


  public int getCode() {
    return code;
  }


  public boolean isOk() {
    return ok;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getAlert() {
    return alert;
  }

  public void setAlert(String alert) {
    this.alert = alert;
  }
}
