package net.yan100.compose.security.oauth2.properties;

import lombok.Data;

@Data
public class WxpaProperties {
    /**
     * 验证服务器的配置 token
     */
    public String verifyToken;
    public String appId;
    public String appSecret;
    /**
     * 微信固定的 api 过期时间，一般不需要调整
     */
    private Long fixedExpiredSecond = 700_0L;
}
