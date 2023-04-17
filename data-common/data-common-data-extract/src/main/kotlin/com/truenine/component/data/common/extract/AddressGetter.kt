package com.truenine.component.data.common.extract

import com.truenine.component.core.annotations.BetaTest
import com.truenine.component.core.lang.LogKt
import com.truenine.component.core.lang.Str
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL
import java.time.Duration
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit

// TODO 动态抽取地址
@BetaTest
class AddressGetter(
  private val persistenceCallback: (
    ha: List<Pair<URL?, Pair<String, String>>>,
    parent: Pair<URL?, Pair<String, String>>?, level: Int
  ) -> Unit,
) {
  private var baseUrl =
    "http://www.stats.gov.cn/sj/tjbz/tjyqhdmhcxhfdm/2022/index.html"
  private val client = OkHttpClient().newBuilder()
    .writeTimeout(Duration.ofSeconds(300))
    .readTimeout(Duration.ofSeconds(300))
    .callTimeout(Duration.ofSeconds(300))
    .connectTimeout(Duration.ofSeconds(300))
    .build()

  private val provinces =
    ArrayBlockingQueue<Pair<URL?, Pair<String, String>>>(3267)
  private val cities =
    ArrayBlockingQueue<Pair<URL?, Pair<String, String>>>(3267)
  private val counties =
    ArrayBlockingQueue<Pair<URL?, Pair<String, String>>>(3267)
  private val towns =
    ArrayBlockingQueue<Pair<URL?, Pair<String, String>>>(3267)
  private val villages =
    ArrayBlockingQueue<Pair<URL?, Pair<String, String>>>(3267)

  private fun req(
    url: URL?,
    dispatch: (body: Document) -> List<Pair<URL?, Pair<String, String>>>
  ): List<Pair<URL?, Pair<String, String>>> {
    if (url == null) {
      return listOf()
    }
    val request = Request.Builder()
      .method(method = "GET", null)
      .url(url)
      .build()
    val data = client
      .newCall(request)
      .execute()
      .run {
        if (isSuccessful) {
          val html = body.string()
          if (Str.hasText(html)) {
            dispatch(Jsoup.parse(html))
          } else error("没有获取到列表")
        } else error("请求错误 ${this.request.url} ${this.code} ${this.body}")
      }
    return data
  }

  fun bootUp() = runBlocking {
    launch(this@AddressGetter.provinces)
    baseUrl = baseUrl.replace("index.html", "")
    val jobs = listOf(
      launch { processCity(provinces, cities) },
      launch { processCounties(cities, counties) },
      launch { processTowns(counties, towns) },
      //launch { processVillages(towns, villages) }
    )
    jobs.joinAll()
  }

  private fun launch(
    provinces: BlockingQueue<Pair<URL?, Pair<String, String>>>
  ) {
    val l = req(URL(baseUrl)) { entrance ->
      entrance.body().selectXpath("//tr[@class='provincetr']/td/a")
        .map { link ->
          val uri = link.attr("href")
          val url = baseUrl.replace("index.html", "") + uri
          val code = uri.replace(".html", "0000000000")
          val name = link.text()
          URL(url) to (code to name)
        }
    }
    persistenceCallback(l, null, 1)
    l.forEach {
      while (!provinces.offer(it, 3, TimeUnit.SECONDS)) {
      }
    }
  }

  private suspend fun processCity(
    provinces: BlockingQueue<Pair<URL?, Pair<String, String>>>,
    cities: BlockingQueue<Pair<URL?, Pair<String, String>>>
  ) {
    while (true) {
      val province = withContext(Dispatchers.IO)
      { provinces.poll(3, TimeUnit.SECONDS) } ?: break
      getEntity("citytr", 2, province, cities) {
        URL(baseUrl + it)
      }
    }
  }

  private suspend fun processCounties(
    cities: BlockingQueue<Pair<URL?, Pair<String, String>>>,
    counties: BlockingQueue<Pair<URL?, Pair<String, String>>>,
  ) {
    while (true) {
      val city = withContext(Dispatchers.IO)
      { cities.poll(3, TimeUnit.SECONDS) } ?: break
      getEntity("countytr", 3, city, counties) {
        val u = city.first
        val pageRoot = u?.file?.split("/")
          ?.last()?.substring(0, 2)
        URL("$baseUrl$pageRoot/$it")
      }
    }
  }

  private suspend fun processTowns(
    counties: BlockingQueue<Pair<URL?, Pair<String, String>>>,
    towns: BlockingQueue<Pair<URL?, Pair<String, String>>>
  ) {
    while (true) {
      val county = withContext(Dispatchers.IO)
      { counties.poll(3, TimeUnit.SECONDS) } ?: break
      getEntity("towntr", 4, county, towns) {
        val u = county.first
        val pageRoot = u?.file?.split("/")
          ?.last()?.substring(0, 2)
        val towRoot = u?.file?.split("/")
          ?.last()?.substring(2, 4)
        URL("$baseUrl$pageRoot/$towRoot/$it")
      }
    }
  }


  private suspend fun processVillages(
    towns: BlockingQueue<Pair<URL?, Pair<String, String>>>,
    villages: BlockingQueue<Pair<URL?, Pair<String, String>>>
  ) {
    while (true) {
      val town = withContext(Dispatchers.IO)
      { towns.poll(3, TimeUnit.SECONDS) } ?: break
      val l = req(town.first) { html ->
        html.body().selectXpath("//tr[@class='villagetr']").mapNotNull {
          val code = it.child(0).text()
          val name = it.child(2).text()
          val link: URL? = null
          link to (code to name)
        }
      }
      persistenceCallback(l, town, 5)
    }
  }

  private fun getEntity(
    seName: String,
    lev: Int,
    parent: Pair<URL?, Pair<String, String>>,
    queue: BlockingQueue<Pair<URL?, Pair<String, String>>>,
    linkHandle: (String) -> URL?
  ) {
    val l = req(parent.first) { html ->
      html.body().selectXpath("//tr[@class='$seName']")
        .mapNotNull { pair ->
          val code = pair.child(0).text()
          val name = pair.child(1).text()
          val uri = pair.child(1).selectXpath("a")
            .attr("href")
          val link = if (Str.hasText(uri)) linkHandle(uri) else null
          link to (code to name)
        }
    }
    persistenceCallback(l, parent, lev)
    l.forEach {
      while (!queue.offer(it, 3, TimeUnit.SECONDS)) {
      }
    }
  }

  companion object {
    private val log = LogKt.getLog(AddressGetter::class)
  }
}
