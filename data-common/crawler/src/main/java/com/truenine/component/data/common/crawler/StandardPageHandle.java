package com.truenine.component.data.common.crawler;

import com.truenine.component.core.api.http.Methods;
import com.truenine.component.data.common.crawler.annotations.PagePath;
import com.truenine.component.data.common.crawler.downloader.PageContent;
import com.truenine.component.data.common.crawler.downloader.TaskInfo;
import com.truenine.component.data.common.crawler.jsoup.WrappedDocument;
import com.truenine.component.data.common.crawler.selenium.NamedWrapperDriver;
import com.truenine.component.data.common.crawler.selenium.WrappedDriver;
import com.truenine.component.data.common.crawler.util.BusChecks;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 标准页面控制器
 *
 * @author TrueNine
 * @since 2022-11-03
 */
@PagePath
@Slf4j
public class StandardPageHandle extends BasePageHandle {
  private final List<TaskInfo> taskInfos = new CopyOnWriteArrayList<>();
  private final PageContent pageContent;
  private final Map<String, Object> dataMap = new ConcurrentHashMap<>();
  private NamedWrapperDriver dynamicNode;

  public StandardPageHandle(PageContent pageContent,
                            NamedWrapperDriver node) {
    this.pageContent = pageContent;
    this.dynamicNode = node;
  }

  @Override
  public String getRequestUrl() {
    return pageContent.getUrl();
  }

  @Override
  public void addDynamicTask(Consumer<WrappedDriver> nextTaskSwitch, String requestUrl, String route) {
    if (Objects.isNull(dynamicNode)) {
      log.warn("没有分配可用的 NamedDriver url =  {},route = {}", requestUrl, route);
      addStaticTask(requestUrl, route);
    } else {
      nextTaskSwitch.accept(dynamicNode.driver());

      var taskInfo = new TaskInfo();
      taskInfo.setUrl(requestUrl)
        .setDynamic(true)
        .setDynamicNode(dynamicNode)
        .setRouteTo(BusChecks.extractRoute(route))
        .setMethod(Methods.GET);
      this.taskInfos.add(taskInfo);
    }
  }

  @Override
  public void addDynamicTask(Consumer<WrappedDriver> nextTaskSwitch, String route) {
    addDynamicTask(nextTaskSwitch, null, route);
  }

  @Override
  public void addStaticTask(String requestUrl, String... routes) {
    addStaticTask(requestUrl,
      new HashMap<>(100),
      Methods.GET,
      routes);
  }

  @Override
  public void addStaticTask(String requestUrl,
                            Map<String, String> headers,
                            String method,
                            String... routes) {
    var taskInfo = new TaskInfo()
      .setDynamic(false)
      .setUrl(requestUrl)
      .setMethod(method)
      .setHeaders(headers)
      .setRouteTo(BusChecks.extractRoute(routes));
    taskInfo.setHeaders(headers);
    this.taskInfos.add(taskInfo);
  }

  @Override
  public void addNamedData(String name, Object data) {
    dataMap.put(name, data);
  }

  @Override
  public WrappedDocument freeze() {
    if (Objects.nonNull(dynamicNode)) {
      return WrappedDocument.wrapper(dynamicNode.driver());
    } else {
      return WrappedDocument.wrapper(pageContent.getRawText());
    }
  }

  @Override
  public Map<String, Object> getDataMap() {
    return dataMap;
  }

  @Override
  public PageContent toPageContent() {
    if (Objects.nonNull(dynamicNode)) {
      this.pageContent.setRawText(
        dynamicNode.driver().allHtml()
      );
    }
    return this.pageContent;
  }

  @Override
  public List<TaskInfo> allTask() {
    return this.taskInfos;
  }

  @Override
  public NamedWrapperDriver namedDriver() {
    return this.dynamicNode;
  }

  @Override
  public void trimmingContent(Function<PageContent, PageContent> handle) {
    this.pageContent.setRawText(
      handle.apply(this.pageContent).getRawText()
    );
  }

  @Override
  public void setNamedDriver(NamedWrapperDriver driver) {
    this.dynamicNode = driver;
  }

  @Override
  public WrappedDocument document() {
    return this.pageContent.toDocument();
  }
}
