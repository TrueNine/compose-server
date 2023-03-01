package com.truenine.component.data.common.crawler.util;

import com.truenine.component.data.common.crawler.annotation.EnableDynamicCrawler;
import com.truenine.component.data.common.crawler.annotations.PagePath;
import com.truenine.component.data.common.crawler.CrawlerPageProcessor;
import com.truenine.component.data.common.crawler.downloader.StandardStaticDownloader;
import io.tn.core.lang.ContainerUtil;
import io.tn.core.lang.Reflects;
import io.tn.core.lang.Str;

import java.util.Collection;
import java.util.Optional;

/**
 * 总线使用的一些检测方法
 *
 * @author TrueNine
 * @since 2022-10-11
 */
public class BusChecks {

  public static final String DEFAULT_ROOT = PagePath.ROOT_PATH;

  public static String checkAndReturnDefaultRoute(Class<?> cls) {
    if (cls.isAnnotationPresent(PagePath.class)) {
      var ruteAnno = cls.getAnnotation(PagePath.class);
      return defaultRoutePath(
        Optional.of(ruteAnno.value())
          .orElse(ruteAnno.path())
      );
    } else {
      return DEFAULT_ROOT;
    }
  }

  public static String defaultRoutePath(String route) {
    return Str.hasText(route) ? route : DEFAULT_ROOT;
  }

  public static PagePath getPagePath(CrawlerPageProcessor processor) {
    var pagePath = Reflects.getAnnotationFromClass(processor.getClass(), PagePath.class);
    if (null == pagePath) {
      return StandardStaticDownloader.class.getAnnotation(PagePath.class);
    }
    return pagePath;
  }

  public static boolean checkAllClassAnnotationDynamic() {
    return Reflects.getAnnotatedAllClass(EnableDynamicCrawler.class).size() > 0;
  }

  public static boolean checkAllProcessorContainsDynamic(Collection<CrawlerPageProcessor> pageProcessors) {
    return pageProcessors.stream()
      .map(f -> f.getClass()
        .getAnnotation(PagePath.class).dynamic())
      .reduce((a, b) -> a || b).orElse(false);
  }

  public static boolean pageProcessorIsDynamic(CrawlerPageProcessor processor) {
    var anno = Optional.ofNullable(processor.getClass().getAnnotation(PagePath.class))
      .orElse(StandardStaticDownloader.class.getAnnotation(PagePath.class));
    return anno.dynamic();
  }

  public static String extractRoute(String... routes) {
    return ContainerUtil.isNullOrEmpty(routes)
      ? DEFAULT_ROOT : routes[0];
  }
}
