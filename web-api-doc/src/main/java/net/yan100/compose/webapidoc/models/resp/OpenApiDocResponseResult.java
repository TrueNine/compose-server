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
package net.yan100.compose.webapidoc.models.resp;

/*{
    "configUrl": "/v3/api-docs/swagger-config",
    "oauth2RedirectUrl": "http://localhost:8080/swagger-ui/oauth2-redirect.html",
    "urls": [
        {
            "url": "/v3/api-docs/default",
            "name": "default"
        },
        {
            "url": "/v3/api-docs/swagger",
            "name": "swagger"
        }
    ],
    "validatorUrl": ""
}*/

import java.util.List;
import lombok.Data;

/**
 * 开放api文档签证官
 *
 * @author TrueNine
 * @since 2022-10-03
 */
@Data
public class OpenApiDocResponseResult {
    String configUrl;
    String oauth2RedirectUrl;
    List<OpenApiUrlsResponseResult> urls;
}
