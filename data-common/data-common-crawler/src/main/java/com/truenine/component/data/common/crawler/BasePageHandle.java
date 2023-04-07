package com.truenine.component.data.common.crawler;

import com.truenine.component.data.common.crawler.downloader.PageContent;
import com.truenine.component.data.common.crawler.downloader.TaskInfo;
import com.truenine.component.data.common.crawler.jsoup.WrappedDocument;
import com.truenine.component.data.common.crawler.selenium.NamedWrapperDriver;
import com.truenine.component.data.common.crawler.selenium.WrappedDriver;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 摘要页面控制器
 *
 * @author TrueNine
 * @since 2022-10-28
 */
public abstract class BasePageHandle {
  public abstract String getRequestUrl();

  /**
   * 添加动态任务
   *
   * @param requestUrl     请求url
   * @param route          路由路径
   * @param nextTaskSwitch 下一个任务切换
   */
  public abstract void addDynamicTask(Consumer<WrappedDriver> nextTaskSwitch, String requestUrl, String route);

  /**
   * 添加动态任务，但是没有 url
   *
   * @param routes         路由路径
   * @param nextTaskSwitch 下一个任务切换
   */
  public abstract void addDynamicTask(Consumer<WrappedDriver> nextTaskSwitch, String routes);

  /**
   * 添加静态任务
   *
   * @param requestUrl 请求url
   * @param route      路由路径
   */
  public abstract void addStaticTask(String requestUrl, String... route);

  /**
   * 添加静态任务
   *
   * @param requestUrl 请求url
   * @param headers    头
   * @param route      路线
   */
  public abstract void addStaticTask(
    String requestUrl,
    Map<String, String> headers,
    String method, String... route);

  /**
   * 添加一个命名数据
   *
   * @param name key
   * @param data 数据
   */
  public abstract void addNamedData(String name, Object data);

  /**
   * 将当前的动态内容转换为静态，以提升效率
   * <br/>
   * 很多时候需要配合插桩使用
   *
   * @return {@link WrappedDocument}
   */
  public abstract WrappedDocument freeze();

  /**
   * 获取数据地图
   *
   * @return {@link Map}<{@link String}, {@link Object}>
   */
  public abstract Map<String, Object> getDataMap();

  /**
   * 转换为页面内容
   *
   * @return {@link PageContent}
   */
  public abstract PageContent toPageContent();

  /**
   * 所有的任务
   *
   * @return {@link List}<{@link TaskInfo}>
   */
  public abstract List<TaskInfo> allTask();

  /**
   * 获取 动态页面的 Driver
   *
   * @return {@link NamedWrapperDriver}
   */
  public abstract NamedWrapperDriver namedDriver();

  /**
   * 调整内容
   *
   * @param handle 处理函数
   */
  public abstract void trimmingContent(Function<PageContent, PageContent> handle);

  /**
   * 设置一个命名过的 driver，提供的driver会在执行完 process 后被销毁
   *
   * @param driver {@link NamedWrapperDriver}
   */
  public abstract void setNamedDriver(NamedWrapperDriver driver);

  public abstract WrappedDocument document();
}
