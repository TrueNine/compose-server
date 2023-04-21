package com.truenine.component.datacommon.dataextract.common.crawler.pipelines;

import com.google.common.io.Files;
import com.truenine.component.datacommon.dataextract.common.crawler.annotations.PagePath;
import com.truenine.component.datacommon.dataextract.common.crawler.downloader.ThisTaskDetails;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件保存的 pipeline
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@PagePath
public class SimpleFilePipeline implements CrawlerPipeline {

  private final File directory;
  private String suffix;

  public SimpleFilePipeline(File dir, String fileSuffix) {
    directory = dir;
    this.suffix = fileSuffix;
  }

  @Override
  public void saveTo(ResultData data, ThisTaskDetails details) {
    var uid = UUID.randomUUID().toString();
    var htmlFile = new File(directory.getAbsoluteFile(), uid + this.suffix);
    try {
      Files.write(data.getRawText().getBytes(), htmlFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
