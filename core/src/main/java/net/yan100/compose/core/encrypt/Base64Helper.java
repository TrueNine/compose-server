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
        return new String(
            Base64.decode(base64),
            charset
        );
    }
}
