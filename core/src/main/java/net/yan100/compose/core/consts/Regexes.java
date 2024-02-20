/*
 * ## Copyright (c) 2024 TrueNine. All rights reserved.
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
 *     Email: <truenine304520@gmail.com>
 *     Website: [gitee.com/TrueNine]
 */
package net.yan100.compose.core.consts;

/**
 * 常用正则表达式常量
 *
 * @author TrueNine
 * @since 2023-04-19
 */
public interface Regexes {
    /**
     * 英文数字账户
     */
    String ACCOUNT = "^[a-zA-Z0-9]+$";

    /**
     * 密码
     */
    String PASSWORD = "^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[^\\da-zA-Z\\s]).{1,9}$";

    /**
     * 中国的手机
     */
    String CHINA_PHONE = "^1[123456789]\\d{9}$";
}
