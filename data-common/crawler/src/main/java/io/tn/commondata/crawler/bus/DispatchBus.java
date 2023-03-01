package io.tn.commondata.crawler.bus;

import io.tn.commondata.crawler.BasePageHandle;
import io.tn.commondata.crawler.CrawlerPageProcessor;
import io.tn.commondata.crawler.downloader.*;
import io.tn.commondata.crawler.pipelines.ConsoleAndLogPipeline;
import io.tn.commondata.crawler.pipelines.CrawlerPipeline;
import io.tn.commondata.crawler.pipelines.ResultData;
import io.tn.commondata.crawler.schedulers.CrawlerContentScheduler;
import io.tn.commondata.crawler.schedulers.InMemorySchedulerQueue;
import io.tn.commondata.crawler.selenium.ChromiumDriverOps;
import io.tn.commondata.crawler.selenium.DriverExecutePool;
import io.tn.commondata.crawler.util.BusChecks;
import io.tn.core.lang.DTimer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * 所有公共汽车
 *
 * @author TrueNine
 * @since 2022-11-01
 */
@Slf4j
public class DispatchBus {
  private final Map<String, CrawlerPageProcessor> pageProcessorMap = new ConcurrentHashMap<>();
  private final Map<String, CrawlerDynamicDownloader> dynamicDownloaderMap = new ConcurrentHashMap<>();
  private final Map<String, CrawlerStaticDownloader> staticDownloaderMap = new ConcurrentHashMap<>();
  private final Map<String, CrawlerContentScheduler> schedulerMap = new ConcurrentHashMap<>();
  private final Map<String, CrawlerPipeline> pipelineMap = new ConcurrentHashMap<>();
  private final ThreadPoolExecutor savePool;
  private final long WAIT_SECOND;
  private DriverExecutePool dynamicExecutePool;
  private boolean busIsDynamic = false;


  public DispatchBus(boolean busIsDynamic,
                     int seleniumCount,
                     int downloaderMaxThread,
                     int waitSecond,
                     ChromiumDriverOps.DriverType driverType,
                     ChromiumDriverOps.Builder builder) {
    WAIT_SECOND = waitSecond;
    initDefaultBus();
    if (busIsDynamic) {
      this.busIsDynamic = true;
      initSeleniumPool(driverType, seleniumCount, builder);
    }

    BlockingQueue<Runnable> threadQueue = new LinkedBlockingQueue<>();
    savePool = new ThreadPoolExecutor(
      downloaderMaxThread / 2,
      downloaderMaxThread,
      waitSecond,
      TimeUnit.SECONDS,
      threadQueue,
      r -> new Thread(r));
  }

  public void addPipeline(CrawlerPipeline pipeline) {
    var route = BusChecks.checkAndReturnDefaultRoute(pipeline.getClass());
    this.pipelineMap.put(route, pipeline);
  }

  public void addDynamicDownloader(CrawlerDynamicDownloader downloader) {
    dynamicDownloaderMap.put(BusChecks.checkAndReturnDefaultRoute(downloader.getClass()), downloader);
  }

  public void addScheduler(CrawlerContentScheduler scheduler) {
    schedulerMap.put(BusChecks.checkAndReturnDefaultRoute(scheduler.getClass()), scheduler);
  }

  public void addStaticDownloader(CrawlerStaticDownloader downloader) {
    staticDownloaderMap.put(BusChecks.checkAndReturnDefaultRoute(downloader.getClass()), downloader);
  }

  /**
   * 添加一个页面处理器
   *
   * @param processor 处理器
   */
  public void addPageProcessor(CrawlerPageProcessor processor) {
    var route = BusChecks.checkAndReturnDefaultRoute(processor.getClass());
    if (pageProcessorMap.containsKey(route)) {
      log.warn("路由路径 {} 已存在相同 pageProcessor，因此没有添加", route);
      return;
    }
    pageProcessorMap.put(BusChecks.checkAndReturnDefaultRoute(processor.getClass()), processor);
  }

  /**
   * 添加一条默认处理器
   */
  private void initDefaultBus() {
    addStaticDownloader(new StandardStaticDownloader());
    addDynamicDownloader(new StandardDynamicDownloader());
    addScheduler(new InMemorySchedulerQueue());
    addPipeline(new ConsoleAndLogPipeline());
  }


  private void initSeleniumPool(ChromiumDriverOps.DriverType type,
                                int count,
                                ChromiumDriverOps.Builder builder) {
    if (busIsDynamic) {
      dynamicExecutePool = DriverExecutePool.createPool(type, count, builder);
    }
  }

  public List<TaskInfo> runTask(TaskInfo taskInfo) {
    return runTask(getPageProcessor(taskInfo.getRouteTo()), taskInfo);
  }

  public List<TaskInfo> runTask(CrawlerPageProcessor processor,
                                TaskInfo taskInfo) {
    // 获得路由路径
    var route = BusChecks.defaultRoutePath(taskInfo.getRouteTo());

    // 获取动静下载器
    var staticDownloader = getStaticDownloader(route);
    var dynamicDownloader = getDynamicDownloader(route);

    log.debug("暂停 {} 秒后，执行任务 {}", WAIT_SECOND, taskInfo);
    DTimer.sleep(WAIT_SECOND);

    // 检查下载器，并且获取操作句柄
    BasePageHandle handle = beforeCheckingExecuteDownload(
      staticDownloader,
      dynamicDownloader,
      taskInfo,
      busIsDynamic);

    // 没有 找到对应的 processor 则直接返回一个空列表
    if (Objects.isNull(processor)) {
      log.error("没有找到 {} 路径的 PageProcess", taskInfo.getRouteTo());
      dynamicExecutePool.destroy(handle.namedDriver());
      return new ArrayList<>();
    }
    PageContent pageContent;
    try {
      pageContent = processor.process(handle);
    } catch (Throwable e) {
      log.warn("总线执行任务异常,直接返回空任务列表任务", e);
      dynamicExecutePool.destroy(handle.namedDriver());
      return new ArrayList<>();
    }

    var details = processor.describeTaskDetails(new ThisTaskDetails.Builder());

    // 回收 driver
    if (Objects.nonNull(handle.namedDriver())) {
      dynamicExecutePool.destroy(handle.namedDriver());
    }

    // 进入去重过滤器
    var scheduler = getScheduler(route);
    var notRepeat = !scheduler.contentIsRepeated(pageContent, details);
    var pipeline = getPipeline(route);

    // 开辟线程进行存储
    if (notRepeat) {
      var data = new ResultData()
        .setRawText(pageContent.getRawText())
        .setDataMap(handle.getDataMap())
        .setTaskInfo(taskInfo)
        .setDetails(details);
      savePool.execute(() -> pipeline.saveTo(data, details));
    }
    return handle.allTask();
  }

  /**
   * 关闭总线以及回收资源
   *
   * @throws IOException ioexception
   */
  public void shutdown() throws IOException {
    // 等待保存任务完成
    savePool.shutdown();
    while (!savePool.isTerminated()) {
    }

    // 等待动态任务执行完成
    if (Objects.nonNull(this.dynamicExecutePool)) {
      dynamicExecutePool.close();
    }
    // 卸载所有队列
    pageProcessorMap.clear();
    pipelineMap.clear();
    staticDownloaderMap.clear();
  }

  /**
   * 在检查之前执行下载
   *
   * @param staticDownloader  静态下载器
   * @param dynamicDownloader 动态下载器
   * @param taskInfo          请求任务
   * @param dynamic           是否为动态请求
   * @return {@link BasePageHandle}
   */
  private BasePageHandle beforeCheckingExecuteDownload(
    CrawlerStaticDownloader staticDownloader,
    CrawlerDynamicDownloader dynamicDownloader,
    TaskInfo taskInfo,
    boolean dynamic
  ) {
    var driver = taskInfo.getDynamicNode();
    var request = taskInfo.getRequest();
    BasePageHandle handle;
    if (dynamic && taskInfo.isDynamic()) {
      if (Objects.isNull(dynamicDownloader)) {
        dynamicDownloader = dynamicDownloaderMap.get(BusChecks.DEFAULT_ROOT);
      }

      var node = Optional.ofNullable(driver)
        .orElse(dynamicExecutePool.driver());
      taskInfo.setDynamicNode(node);
      dynamicDownloader.preProcess(request, node);
      handle = dynamicDownloader.dynamicDownload(request, node);
      dynamicDownloader.postProcess(request, node);

      // 注入 driver 到 handle
      handle.setNamedDriver(node);
    } else if (Objects.nonNull(staticDownloader)) {
      staticDownloader.preProcess(request, null);
      handle = staticDownloader.staticDownload(request, null);
      staticDownloader.postProcess(request, null);
      // 没有任何路径匹配则直接进行静态返回
    } else {
      var defaultStaticDownloader = staticDownloaderMap.get(BusChecks.DEFAULT_ROOT);
      defaultStaticDownloader.preProcess(request, null);
      handle = defaultStaticDownloader.staticDownload(request, null);
      defaultStaticDownloader.postProcess(request, null);
    }
    return handle;
  }

  public CrawlerDynamicDownloader getDynamicDownloader(String route) {
    return Optional.ofNullable(this.dynamicDownloaderMap.get(route))
      .orElse(this.dynamicDownloaderMap.get(BusChecks.DEFAULT_ROOT));
  }

  public CrawlerStaticDownloader getStaticDownloader(String route) {
    return Optional.ofNullable(this.staticDownloaderMap.get(route))
      .orElse(this.staticDownloaderMap.get(BusChecks.DEFAULT_ROOT));
  }

  public CrawlerPageProcessor getPageProcessor(String route) {
    return Optional.ofNullable(pageProcessorMap.get(route))
      .orElse(pageProcessorMap.get(BusChecks.DEFAULT_ROOT));
  }

  public CrawlerContentScheduler getScheduler(String route) {
    return Optional.ofNullable(this.schedulerMap.get(route))
      .orElse(this.schedulerMap.get(BusChecks.DEFAULT_ROOT));
  }

  public CrawlerPipeline getPipeline(String route) {
    return Optional.ofNullable(this.pipelineMap.get(route))
      .orElse(this.pipelineMap.get(BusChecks.DEFAULT_ROOT));
  }
}
