package com.truenine.component.datacommon.dataextract.common.crawler.pipelines;

import com.truenine.component.datacommon.dataextract.common.crawler.downloader.TaskInfo;
import com.truenine.component.datacommon.dataextract.common.crawler.downloader.ThisTaskDetails;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jsoup.nodes.Document;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 结果数据
 *
 * @author TrueNine
 * @since 2022-10-29
 */
@Data
@Accessors(chain = true)
public class ResultData {
  private TaskInfo taskInfo;
  private ThisTaskDetails details;
  private Map<String, Object> dataMap = new ConcurrentHashMap<>();
  private String rawText;
  private Document dom;

  @SuppressWarnings("unchecked")
  public <T> T getData(String name) {
    return (T) dataMap.get(name);
  }
}