package io.tn.commondata.crawler;

import io.tn.commondata.crawler.CrawlerPageProcessor;
import io.tn.commondata.crawler.bus.DispatchBus;
import io.tn.commondata.crawler.downloader.CrawlerDynamicDownloader;
import io.tn.commondata.crawler.downloader.CrawlerStaticDownloader;
import io.tn.commondata.crawler.downloader.TargetRequest;
import io.tn.commondata.crawler.downloader.TaskInfo;
import io.tn.commondata.crawler.pipelines.CrawlerPipeline;
import io.tn.commondata.crawler.schedulers.CrawlerContentScheduler;
import io.tn.commondata.crawler.selenium.ChromiumDriverOps;
import io.tn.commondata.crawler.util.BusChecks;
import io.tn.core.api.http.Methods;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 爬虫启动器
 *
 * @author TrueNine
 * @date 2022-10-28
 */
@Slf4j
@ToString
public class CrawlerLauncher {
  private final Map<String, CrawlerPageProcessor> pageProcessorMap = new ConcurrentHashMap<>();
  private final Map<String, CrawlerDelegateProcessor<?>> delegateMap = new ConcurrentHashMap<>();
  private final List<CrawlerDynamicDownloader> dynamicDownloads = new CopyOnWriteArrayList<>();
  private final List<CrawlerStaticDownloader> staticDownloads = new CopyOnWriteArrayList<>();
  private final List<CrawlerContentScheduler> schedulers = new CopyOnWriteArrayList<>();
  private final List<CrawlerPipeline> pipelines = new CopyOnWriteArrayList<>();

  private final AtomicInteger DEEPIN_LEVEL = new AtomicInteger(1024);
  private int seleniumCount = 1;
  private int waitSecond = 1;
  private int downloaderMaxThread = 300;
  private ChromiumDriverOps.DriverType driverType = ChromiumDriverOps.DriverType.CHROME;
  private ChromiumDriverOps.Builder optionsBuilder = ChromiumDriverOps::dev;
  private TaskInfo firstTask = new TaskInfo();

  private DispatchBus bus;


  private CrawlerLauncher() {
  }

  /**
   * 获取一个启动器实例
   *
   * @return {@link CrawlerLauncher}
   */
  public static CrawlerLauncher create() {
    return new CrawlerLauncher();
  }

  public CrawlerLauncher maxPipelineExecutePoolSize(int size) {
    downloaderMaxThread = size;
    return this;
  }

  public CrawlerLauncher request(TargetRequest request, String firstRoute) {
    this.firstTask.setRequest(request);
    return firstRouteTo(firstRoute);
  }

  public CrawlerLauncher waitSecond(int waitSecond) {
    this.waitSecond = waitSecond;
    return this;
  }

  public CrawlerLauncher browserCount(int seleniumCount) {
    this.seleniumCount = seleniumCount;
    return this;
  }

  public CrawlerLauncher browserOptions(ChromiumDriverOps.Builder builder) {
    this.optionsBuilder = builder;
    return this;
  }

  public CrawlerLauncher browserType(ChromiumDriverOps.DriverType type) {
    this.driverType = type;
    return this;
  }

  public CrawlerLauncher setTask(TaskInfo task) {
    firstTask = task;
    return this;
  }

  public CrawlerLauncher firstRouteTo(String route) {
    firstTask.setRouteTo(route);
    return this;
  }

  public CrawlerLauncher addPipeline(CrawlerPipeline pipeline) {
    pipelines.add(pipeline);
    return this;
  }

  public CrawlerLauncher addPageProcessor(CrawlerPageProcessor processor) {
    pageProcessorMap.put(BusChecks.checkAndReturnDefaultRoute(processor.getClass()), processor);
    return this;
  }

  public CrawlerLauncher addDynamicDownloader(CrawlerDynamicDownloader downloader) {
    dynamicDownloads.add(downloader);
    return this;
  }

  public CrawlerLauncher addStaticDownloader(CrawlerStaticDownloader downloader) {
    staticDownloads.add(downloader);
    return this;
  }

  public void runAsync() {
    new Thread(this::run).start();
  }

  public void run() {
    run(firstTask);
  }

  public void runWithPageProcessor(CrawlerPageProcessor pageProcessor,
                                   String url) {
    runWithPageProcessor(pageProcessor, new TaskInfo()
      .setUrl(url)
      .setMethod(Methods.GET));
  }

  public void runWithPageProcessor(CrawlerPageProcessor pageProcessor,
                                   TaskInfo taskInfo) {
    if (Objects.isNull(bus)) {
      log.error("未进行总线初始化，请使用 submit() 提交配置");
      return;
    }
    if (Objects.isNull(pageProcessor)) {
      log.error("未找到任务 {} 的 pageProcessor", taskInfo);
      return;
    }

    // 检测当前页面是否为 动态页面，根据注解
    var isDynamic = BusChecks.pageProcessorIsDynamic(pageProcessor);
    taskInfo.setDynamic(isDynamic);

    // 获取当前路由
    var route = BusChecks.checkAndReturnDefaultRoute(pageProcessor.getClass());
    taskInfo.setRouteTo(route);

    log.info("执行任务 {}", taskInfo);
    var tasks = bus.runTask(pageProcessor, taskInfo);
    for (TaskInfo task : tasks) {
      run(task);
    }
  }

  /**
   * 设定详细的任务参数进行请求
   *
   * @param taskInfo 任务信息
   */
  public void run(TaskInfo taskInfo) {
    runWithPageProcessor(pageProcessorMap.get(taskInfo.getRouteTo()), taskInfo);
  }

  /**
   * 关闭总线
   */
  public void shutdown() {
    try {
      this.bus.shutdown();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 添加一个内容过滤器
   *
   * @param scheduler 内容过滤器
   * @return {@link CrawlerLauncher}
   */
  public CrawlerLauncher addScheduler(CrawlerContentScheduler scheduler) {
    this.schedulers.add(scheduler);
    return this;
  }

  /**
   * 设置最大递归层级，超过此层级则抛出异常并且推出
   *
   * @param level 层级
   * @return {@link CrawlerLauncher}
   */
  public CrawlerLauncher setDeepinLevel(int level) {
    this.DEEPIN_LEVEL.set(level);
    return this;
  }

  /**
   * 为此次总线任务提交所有配置
   *
   * @return {@link CrawlerLauncher}
   */
  public CrawlerLauncher submit() {
    this.bus = new DispatchBus(
      BusChecks.checkAllProcessorContainsDynamic(pageProcessorMap.values())
        || BusChecks.checkAllClassAnnotationDynamic(),
      seleniumCount,
      downloaderMaxThread,
      waitSecond,
      driverType,
      optionsBuilder
    );
    this.pageProcessorMap.forEach((k, v) -> bus.addPageProcessor(v));
    this.pipelines.forEach(bus::addPipeline);
    this.staticDownloads.forEach(bus::addStaticDownloader);
    this.dynamicDownloads.forEach(bus::addDynamicDownloader);
    this.schedulers.forEach(bus::addScheduler);
    return this;
  }
}
