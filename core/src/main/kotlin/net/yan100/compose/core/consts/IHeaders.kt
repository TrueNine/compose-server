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
package net.yan100.compose.core.consts

import jakarta.annotation.Nullable
import jakarta.servlet.http.HttpServletRequest
import net.yan100.compose.core.IString
import java.net.URLEncoder
import java.nio.charset.Charset

/**
 * http Header Info
 *
 * @author TrueNine
 * @since 2022-10-28
 */
interface IHeaders {
    companion object {
        /**
         * 设置 Content-Disposition 的下载名称 `
         * Content-Disposition: attachment; filename="filename"
        ` *
         *
         * @param fileName 文件名
         * @return attachment; filename="fileName"
         */
        fun downloadDisposition(fileName: String, charset: Charset): String {
            return "attachment; filename=" + URLEncoder.encode(fileName, charset)
        }

        /**
         * 获取用户设备 id，首选 [IHeaders].DEVICE_ID，其次为 [IHeaders].USER_AGENT
         *
         * @param request 请求 id
         * @return 设备 id
         */
        @Nullable
        fun getDeviceId(request: HttpServletRequest): String {
            val deviceId = request.getHeader(X_DEVICE_ID)
            return if (IString.hasText(deviceId)) deviceId else request.getHeader(USER_AGENT)
        }

        const val SERVER: String = "Server"
        const val ACCEPT: String = "Accept"
        const val ACCEPT_ENCODING: String = "Accept-Encoding"
        const val ACCEPT_LANGUAGE: String = "Accept-Language"
        const val COOKIE: String = "Cookie"
        const val HOST: String = "Host"
        const val REFERER: String = "Referer"
        const val USER_AGENT: String = "User-Agent"
        const val X_FORWARDED_FOR: String = "X-Forwarded-For"
        const val X_FORWARDED_PROTO: String = "X-Forwarded-Proto"
        const val PROXY_CLIENT_IP: String = "Proxy-Client-IP"

        /**
         *
         *
         * <h2>真实 ip 地址 </h2>
         *
         *
         * 一般会在 nginx 中设置
         */
        const val X_REAL_IP: String = "X-Real-IP"

        /** 设备 id  */
        const val X_DEVICE_ID: String = "X-Device-Id"

        const val AUTHORIZATION: String = "Authorization"

        /** 自定义刷新头  */
        const val X_REFRESH: String = "X-Refresh"

        /** 需清理过期令牌  */
        const val X_REQUIRE_CLEN_AUTHENTICATION: String = "X-Require-Clean-Authentication"

        /** 微信 open id 授权 自定义id  */
        const val X_WECHAT_AUTHORIZATION_ID: String = "X-Wechat-Authorization-Id"

        const val CONTENT_LENGTH: String = "Content-Length"
        const val CONTENT_TYPE: String = "Content-Type"
        const val CONNECTION: String = "Connection"
        const val CONTENT_DISPOSITION: String = "Content-Disposition"
        const val KEEP_ALIVE: String = "Keep-Alive"

        const val CORS_ALLOW_ORIGIN: String = "Access-Control-Allow-Origin"
        const val CORS_ALLOW_METHODS: String = "Access-Control-Allow-Methods"
        const val CORS_ALLOW_HEADERS: String = "Access-Control-Allow-Headers"
        const val CORS_ALLOW_CREDENTIALS: String = "Access-Control-Allow-Credentials"
    }
}
