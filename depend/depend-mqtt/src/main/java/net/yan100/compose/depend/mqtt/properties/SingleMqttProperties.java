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
package net.yan100.compose.depend.mqtt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@ConfigurationProperties(prefix = "compose.mqtt-client")
public class SingleMqttProperties {
    /**
     * schema = tcp://
     */
    private String url;

    private Integer port = 1883;
    private String clientId = UUID.randomUUID().toString();
    private List<String> topics = new ArrayList<>();
    private String username = "";
    private String password = "";
    private Integer connectTimeoutSecond = 10;
    private Long completionTimeout = 1000L * 5L;
    private Integer qos = 0;
    private Integer keepAliveSecond = 10;

    public String getFullUrl() {
        return getUrl() + ":" + getPort();
    }
}
