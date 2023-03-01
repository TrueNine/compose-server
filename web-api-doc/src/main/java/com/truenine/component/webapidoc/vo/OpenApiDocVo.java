package com.truenine.component.webapidoc.vo;

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

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 开放api文档签证官
 *
 * @author TrueNine
 * @since 2022-10-03
 */
@Data
@Accessors(chain = true)
public class OpenApiDocVo {
  String configUrl;
  String oauth2RedirectUrl;
  List<OpenApiUrlsVo> urls;
}
