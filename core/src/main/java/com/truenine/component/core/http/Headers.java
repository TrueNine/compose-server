package com.truenine.component.core.http;

import com.truenine.component.core.lang.Str;
import jakarta.servlet.http.HttpServletRequest;

import javax.annotation.Nullable;

/**
 * http Header Info
 *
 * @author TrueNine
 * @since 2022-10-28
 */
public class Headers {
  public static final String SERVER = "Server";
  public static final String ACCEPT = "Accept";
  public static final String ACCEPT_ENCODING = "Accept-Encoding";
  public static final String ACCEPT_LANGUAGE = "Accept-Language";
  public static final String COOKIE = "Cookie";
  public static final String HOST = "Host";
  public static final String REFERER = "Referer";
  public static final String USER_AGENT = "User-Agent";
  /**
   * 设备 id
   */
  public static final String X_DEVICE_ID = "X-Device-Id";
  public static final String AUTHORIZATION = "Authorization";

  /**
   * 自定义刷新头
   */
  public static final String X_RE_FLUSH_TOKEN = "X-ReFlushTokenModel-Token";

  public static final String CONTENT_LENGTH = "Content-Length";
  public static final String CONTENT_TYPE = "Content-Type";
  public static final String CONTENT_DISPOSITION = "Content-Disposition";
  public static final String KEEP_ALIVE = "Keep-Alive";

  /**
   * 内部租户路由 id
   */
  public static final String X_INTERNAL_TENANT_ID = "X-Internal-Tenant-Id";

  /**
   * 设置 Content-Disposition 的下载名称
   * <code>
   * Content-Disposition: attachment; filename="filename"
   * </code>
   *
   * @param fileName 文件名
   * @return attachment; filename="fileName"
   */
  public static String downloadDisposition(String fileName) {
    return "attachment; filename=\"" + fileName + "\"";
  }

  /**
   * 获取用户设备 id，首选 {@link Headers}.DEVICE_ID，其次为 {@link Headers}.USER_AGENT
   *
   * @param request 请求 id
   * @return 设备 id
   */
  public static @Nullable String getDeviceId(final HttpServletRequest request) {
    var deviceId = request.getHeader(X_DEVICE_ID);
    return Str.hasText(deviceId) ? deviceId : request.getHeader(USER_AGENT);
  }
}
