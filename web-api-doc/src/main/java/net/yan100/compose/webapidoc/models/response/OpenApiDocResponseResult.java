package net.yan100.compose.webapidoc.models.response;

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

import java.util.List;

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
