package com.truenine.component.data.common.crawler.util;

import com.truenine.component.core.lang.Str;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class WordBreaker {

  public static List<String> split(String text) {
    if (Str.nonText(text)) {
      return new ArrayList<>();
    } else {
      List<String> words = new ArrayList<>(100);
      text = text.replaceAll("\r", "")
        .replaceAll("\n", "")
        .replaceAll("\r\n", "")
        .replaceAll(" ", "");
      try (Analyzer analyzer = new IKAnalyzer(true)) {
        StringReader r = new StringReader(text);
        var ts = analyzer.tokenStream("", r);
        var att = ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        while (ts.incrementToken()) {
          words.add(att.toString());
        }
        return words;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
