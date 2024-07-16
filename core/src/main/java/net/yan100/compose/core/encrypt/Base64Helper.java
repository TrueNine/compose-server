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
package net.yan100.compose.core.encrypt;

import cn.hutool.core.codec.Base64;
import java.nio.charset.Charset;

/**
 * base64 工具类
 *
 * @author TrueNine
 * @since 2023-02-20
 */
public interface Base64Helper {
    /**
     * 编码
     *
     * @param content 内容
     * @return {@link String}
     */
    static String encode(byte[] content) {
        return Base64.encode(content);
    }

    /**
     * 以字节编码
     *
     * @param content 内容
     * @return {@link byte[]}
     */
    static byte[] encodeToByte(byte[] content) {
        return Base64.encode(content).getBytes();
    }

    /**
     * 解码字节
     *
     * @param base64 base64
     * @return {@link byte[]}
     */
    static byte[] decodeToByte(String base64) {
        return Base64.decode(base64);
    }

    /**
     * 解码字节
     *
     * @param base64 base64
     * @return {@link byte[]}
     */
    static String decode(String base64, Charset charset) {
        return new String(Base64.decode(base64), charset);
    }
}
