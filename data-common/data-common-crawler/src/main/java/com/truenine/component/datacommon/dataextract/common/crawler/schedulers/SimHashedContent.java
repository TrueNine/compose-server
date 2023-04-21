package com.truenine.component.datacommon.dataextract.common.crawler.schedulers;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class SimHashedContent {
  private long simHash;
  private String url;
  private String content;
  private List<String> contentWords;
  private List<SimHashedContent> repeatedContents = new ArrayList<>();
}
