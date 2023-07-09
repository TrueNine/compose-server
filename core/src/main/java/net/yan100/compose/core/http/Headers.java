package net.yan100.compose.core.http;

import jakarta.servlet.http.HttpServletRequest;
import net.yan100.compose.core.lang.Str;

import javax.annotation.Nullable;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * http Header Info
 *
 * @author TrueNine
 * @since 2022-10-28
 */
public interface Headers {
  String SERVER = "Server";
  String ACCEPT = "Accept";
  String ACCEPT_ENCODING = "Accept-Encoding";
  String ACCEPT_LANGUAGE = "Accept-Language";
  String COOKIE = "Cookie";
  String HOST = "Host";
  String REFERER = "Referer";
  String USER_AGENT = "User-Agent";
  String X_FORWARDED_FOR = "X-Forwarded-For";
  String PROXY_CLIENT_IP = "Proxy-Client-IP";
  /**
   * 设备 id
   */
  String X_DEVICE_ID = "X-Device-Id";
  String AUTHORIZATION = "Authorization";

  /**
   * 自定义刷新头
   */
  String X_RE_FLUSH_TOKEN = "X-Re-Flush";

  /**
   * 微信 open id 授权 自定义id
   */
  String X_WECHAT_AUTHORIZATION_ID = "X-Wechat-Authorization-Id";

  String CONTENT_LENGTH = "Content-Length";
  String CONTENT_TYPE = "Content-Type";
  String CONTENT_DISPOSITION = "Content-Disposition";
  String KEEP_ALIVE = "Keep-Alive";

  String CORS_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
  String CORS_ALLOW_METHODS = "Access-Control-Allow-Methods";
  String CORS_ALLOW_HEADERS = "Access-Control-Allow-Headers";
  String CORS_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

  /**
   * 设置 Content-Disposition 的下载名称
   * <code>
   * Content-Disposition: attachment; filename="filename"
   * </code>
   *
   * @param fileName 文件名
   * @return attachment; filename="fileName"
   */
  static String downloadDisposition(String fileName, Charset charset) {
    return "attachment; filename=\"" + URLEncoder.encode(fileName, charset) + "\"";
  }

  /**
   * 获取用户设备 id，首选 {@link Headers}.DEVICE_ID，其次为 {@link Headers}.USER_AGENT
   *
   * @param request 请求 id
   * @return 设备 id
   */
  static @Nullable String getDeviceId(final HttpServletRequest request) {
    var deviceId = request.getHeader(X_DEVICE_ID);
    return Str.hasText(deviceId) ? deviceId : request.getHeader(USER_AGENT);
  }
}
