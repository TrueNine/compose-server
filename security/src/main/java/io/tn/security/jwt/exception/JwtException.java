package io.tn.security.jwt.exception;

/**
 * jwt例外
 *
 * @author TrueNine
 * @date 2022-10-28
 */
public class JwtException extends Exception {

    public JwtException(String msg) {
        super(msg);
    }

    public JwtException() {
        super("jwt 未知错误");
    }
}
