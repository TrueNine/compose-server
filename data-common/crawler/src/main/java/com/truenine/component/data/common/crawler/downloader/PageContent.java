package com.truenine.component.data.common.crawler.downloader;

import com.truenine.component.data.common.crawler.jsoup.WrappedDocument;
import io.tn.core.api.http.MediaTypes;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 一个页面的具体内容
 *
 * @author TrueNine
 * @since 2022-11-03
 */
@Data
@Accessors(chain = true)
public class PageContent {
  private String rawText;
  private MediaTypes mimeType;
  private String url;
  private String taskRutePath;
  private List<TaskInfo> nextTasks;

  public WrappedDocument toDocument() {
    return WrappedDocument.wrapper(rawText);
  }
}
