package io.tn.webapidoc.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OpenApiUrlsVo {
    String name;
    String url;
}
