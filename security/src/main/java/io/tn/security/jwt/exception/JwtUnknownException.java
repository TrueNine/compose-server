package io.tn.security.jwt.exception;

public class JwtUnknownException extends JwtException {
    public JwtUnknownException() {
        super("jwt 解析未知 或 未捕获错误");
    }
}
