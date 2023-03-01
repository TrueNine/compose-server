package io.tn.commondata.crawler.pipelines;

import io.tn.commondata.crawler.annotations.PagePath;
import io.tn.commondata.crawler.downloader.ThisTaskDetails;
import lombok.extern.slf4j.Slf4j;

/**
 * 控制台日志管道
 *
 * @author TrueNine
 * @since 2022-10-29
 */
@Slf4j
@PagePath
public class ConsoleAndLogPipeline implements CrawlerPipeline {
  @Override
  public void saveTo(ResultData data, ThisTaskDetails details) {
    log.warn("data {}, 可能没有自己的 Save，或者没有将其添加在上下文当中 ，开启 debug 日志以查看 data", data.getTaskInfo().getRouteTo());
    log.debug("data = {}, details = {}", data, details);
  }
}
