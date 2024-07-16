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
package net.yan100.compose.pay.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * # 微信单支付 js API 配置 <br>
 * <a href="https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_1_1.shtml">微信支付文档</a>
 *
 * @author shanghua
 * @since 2023-05-05
 */
@Data
@ConfigurationProperties(prefix = "compose.pay.wechat")
public class WeChatPayProperties {
    private static final String WECHAT_KEY_DIR = "security/wechat/pay/";

    /** 开启 单配置支付 */
    private Boolean enableSingle = false;

    /** 商户号 */
    private String merchantId = null;

    /** 商户序列号 */
    private String merchantSerialNumber = null;

    /** cret文件存放路径 */
    private String certPath = WECHAT_KEY_DIR + "apiclient_cert.pem";

    /** 私钥文件存放路径 */
    private String privateKeyPath = WECHAT_KEY_DIR + "apiclient_key.pem";

    /** appId 小程序 Id */
    private String mpAppId = null;

    /**
     * 支付成功异步通知 url <br>
     * <a href="https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_1_5.shtml">微信支付通知文档</a>
     */
    private String asyncSuccessNotifyUrl = null;

    /**
     * 异步成功退款通知 url <br>
     * <a href="https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_1_11.shtml">微信退款文档</a>
     */
    private String asyncSuccessRefundNotifyUrl = null;

    /** api 密钥 */
    private String apiSecret = null;

    /** 微信支付 jsAPI v3 私钥 */
    private String apiV3Key = null;
}
