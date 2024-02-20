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
package net.yan100.compose.webapidoc.properties;

import lombok.Data;

@Data
public class SwaggerDescInfo {
    /**
     * 项目名称
     */
    private String title = "OPEN-API3 接口文档";

    /**
     * 项目描述
     */
    private String description = "由 Open-Api 3 提供的项目接口文档";

    /**
     * 项目版本
     */
    private String version = "1.0";

    /**
     * 网址
     */
    private String location = "https://gitee.com/yan100-net";

    /**
     * 作者
     */
    private String author = "YAN100";

    /**
     * 协议名称
     */
    private String license = "不可商用私有协议";

    /**
     * 协议地址
     */
    private String licenseUrl = "https://qq.com";

    /**
     * 分组名称
     */
    private String group = "ABCDEFG";
}
