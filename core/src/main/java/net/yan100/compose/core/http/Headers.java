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
package net.yan100.compose.core.http;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import net.yan100.compose.core.util.Str;

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
    String X_FORWARDED_PROTO = "X-Forwarded-Proto";
    String PROXY_CLIENT_IP = "Proxy-Client-IP";

    /**
     *
     *
     * <h2>真实 ip 地址 </h2>
     *
     * <p>一般会在 nginx 中设置
     */
    String X_REAL_IP = "X-Real-IP";

    /** 设备 id */
    String X_DEVICE_ID = "X-Device-Id";

    String AUTHORIZATION = "Authorization";

    /** 自定义刷新头 */
    String X_REFRESH = "X-Refresh";

    /** 需清理过期令牌 */
    String X_REQUIRE_CLEN_AUTHENTICATION = "X-Require-Clean-Authentication";

    /** 微信 open id 授权 自定义id */
    String X_WECHAT_AUTHORIZATION_ID = "X-Wechat-Authorization-Id";

    String CONTENT_LENGTH = "Content-Length";
    String CONTENT_TYPE = "Content-Type";
    String CONNECTION = "Connection";
    String CONTENT_DISPOSITION = "Content-Disposition";
    String KEEP_ALIVE = "Keep-Alive";

    String CORS_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    String CORS_ALLOW_METHODS = "Access-Control-Allow-Methods";
    String CORS_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    String CORS_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

    /**
     * 设置 Content-Disposition 的下载名称 <code>
     * Content-Disposition: attachment; filename="filename"
     * </code>
     *
     * @param fileName 文件名
     * @return attachment; filename="fileName"
     */
    static String downloadDisposition(String fileName, Charset charset) {
        return "attachment; filename=" + URLEncoder.encode(fileName, charset);
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
