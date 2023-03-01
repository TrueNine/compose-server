package io.tn.commondata.crawler.schedulers;

import io.tn.commondata.crawler.annotations.PagePath;
import io.tn.commondata.crawler.downloader.PageContent;
import io.tn.commondata.crawler.downloader.ThisTaskDetails;
import io.tn.commondata.crawler.util.WordBreaker;
import io.tn.core.lang.Str;
import smile.hash.SimHash;
import smile.math.distance.HammingDistance;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 基于java的去重队列
 *
 * @author TrueNine
 * @since 2022-11-01
 */
@PagePath
public class InMemorySchedulerQueue implements CrawlerContentScheduler {

  private final Map<String, String> URL_REPEATABLE = new ConcurrentHashMap<>();
  private final Map<String, SimHashedContent> CONTENT_REPEATABLE = new ConcurrentHashMap<>();

  public InMemorySchedulerQueue() {
  }

  @Override
  public boolean urlIsRepeated(String url, ThisTaskDetails details) {
    var contains = URL_REPEATABLE.containsKey(url);
    if (!contains) {
      URL_REPEATABLE.put(url, null);
    }
    return contains;
  }

  @Override
  public boolean contentIsRepeated(PageContent pageContent, ThisTaskDetails details) {
    var text = pageContent.toDocument().toString();
    AtomicBoolean repeat = new AtomicBoolean(false);
    if (Str.nonText(text)) {
      return repeat.get();
    } else {
      var key = pageContent.getUrl();
      var words = WordBreaker.split(text);
      final var simHash = SimHash.text().hash(
        words.toArray(String[]::new)
      );
      var content = new io.tn.commondata.crawler.schedulers.SimHashedContent()
        .setUrl(pageContent.getUrl())
        .setContent(pageContent.getRawText())
        .setSimHash(simHash)
        .setContentWords(words);

      // 查询 simHash > all
      CONTENT_REPEATABLE.keySet().forEach(k -> {
        var v = CONTENT_REPEATABLE.get(k);
        var thatSimHash = v.getSimHash();
        if (simHash == thatSimHash) {
          v.getRepeatedContents().add(content);
          repeat.set(false);
        }
        var hammingDistance = HammingDistance.d(simHash, thatSimHash);
        if (hammingDistance < (simHash / 30)) {
          repeat.set(true);
        }
      });
    }
    return repeat.get();
  }

  @Override
  public void close() throws IOException {
    URL_REPEATABLE.clear();
    CONTENT_REPEATABLE.clear();
  }
}
