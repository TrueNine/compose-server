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
package net.yan100.compose.webapidoc.properties;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties(prefix = "compose.web-api-doc.open-api")
public class SwaggerProperties {
    /** 扫描的包 */
    private List<String> scanPackages = new ArrayList<>();

    /** 扫描的路径 */
    private List<String> scanUrlPatterns = new ArrayList<>(List.of("/**"));

    /** 分组名称 */
    private String group = "default";

    /** 开启 jwt 请求头展示 */
    private Boolean enableJwtHeader = false;

    /** jwt 请求头信息 */
    @NestedConfigurationProperty
    private JwtHeaderInfoProperties jwtHeaderInfo = new JwtHeaderInfoProperties();

    /** 类型定义信息 */
    @NestedConfigurationProperty private SwaggerDescInfo authorInfo = new SwaggerDescInfo();
}
