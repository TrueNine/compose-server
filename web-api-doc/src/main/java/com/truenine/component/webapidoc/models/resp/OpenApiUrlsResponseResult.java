package com.truenine.component.webapidoc.models.resp;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OpenApiUrlsResponseResult {
  String name;
  String url;
}
