package io.tn.commondata.crawler.pipelines;

import com.google.common.io.Files;
import io.tn.commondata.crawler.annotations.PagePath;
import io.tn.commondata.crawler.downloader.ThisTaskDetails;
import io.tn.core.id.UUIDGenerator;

import java.io.File;
import java.io.IOException;

/**
 * 文件保存的 pipeline
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@PagePath
public class SimpleFilePipeline implements CrawlerPipeline {

  private final File directory;
  private String suffix = ".html";

  public SimpleFilePipeline(File dir, String fileSuffix) {
    directory = dir;
    this.suffix = fileSuffix;
  }

  @Override
  public void saveTo(ResultData data, ThisTaskDetails details) {
    var uid = UUIDGenerator.str();
    var htmlFile = new File(directory.getAbsoluteFile(), uid + this.suffix);
    try {
      Files.write(data.getRawText().getBytes(), htmlFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
