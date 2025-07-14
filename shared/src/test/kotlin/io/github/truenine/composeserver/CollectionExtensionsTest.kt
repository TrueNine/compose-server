package io.github.truenine.composeserver

import io.github.truenine.composeserver.testtoolkit.log
import java.util.concurrent.ConcurrentHashMap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * # 集合扩展函数测试
 *
 * 测试 CollectionExtensions.kt 中定义的集合相关扩展函数
 */
class CollectionExtensionsTest {

  @Test
  fun `测试 mutableLockMapOf 方法 - 创建带初始值的并发安全Map`() {
    val map = mutableLockMapOf("key1" to "value1", "key2" to "value2", "key3" to "value3")

    log.info("创建的Map类型: {}", map::class.java.simpleName)
    log.info("Map大小: {}", map.size)

    assertTrue(map is ConcurrentHashMap, "应该返回 ConcurrentHashMap 实例")
    assertEquals(3, map.size, "Map大小应该为3")
    assertEquals("value1", map["key1"], "应该包含正确的键值对")
    assertEquals("value2", map["key2"], "应该包含正确的键值对")
    assertEquals("value3", map["key3"], "应该包含正确的键值对")
  }

  @Test
  fun `测试 mutableLockMapOf 方法 - 创建空的并发安全Map`() {
    val map = mutableLockMapOf<String, Int>()

    log.info("空Map类型: {}", map::class.java.simpleName)
    log.info("空Map大小: {}", map.size)

    assertTrue(map is ConcurrentHashMap, "应该返回 ConcurrentHashMap 实例")
    assertEquals(0, map.size, "空Map大小应该为0")
    assertTrue(map.isEmpty(), "Map应该为空")
  }

  @Test
  fun `测试 mutableLockMapOf 方法 - 并发安全性验证`() {
    val map = mutableLockMapOf<String, Int>()

    // 模拟并发操作
    val threads =
      (1..10).map { threadIndex ->
        Thread {
          repeat(100) { iteration ->
            val key = "thread-$threadIndex-item-$iteration"
            map[key] = threadIndex * 100 + iteration
          }
        }
      }

    threads.forEach { it.start() }
    threads.forEach { it.join() }

    log.info("并发操作后Map大小: {}", map.size)
    assertEquals(1000, map.size, "应该包含所有并发插入的元素")
  }

  @Test
  fun `测试 isNotEmptyRun 扩展函数 - 非空集合执行操作`() {
    val list = listOf("a", "b", "c")

    val result =
      list.isNotEmptyRun {
        log.info("集合不为空，执行操作，大小: {}", size)
        this.joinToString(",")
      }

    assertNotNull(result, "非空集合应该执行操作并返回结果")
    assertEquals("a,b,c", result, "应该返回正确的连接字符串")
  }

  @Test
  fun `测试 isNotEmptyRun 扩展函数 - 空集合不执行操作`() {
    val emptyList = emptyList<String>()

    val result =
      emptyList.isNotEmptyRun {
        log.info("这行不应该被执行")
        "should not execute"
      }

    assertNull(result, "空集合应该返回null")
  }

  @Test
  fun `测试 isNotEmptyRun 扩展函数 - null集合不执行操作`() {
    val nullList: List<String>? = null

    val result =
      nullList.isNotEmptyRun {
        log.info("这行不应该被执行")
        "should not execute"
      }

    assertNull(result, "null集合应该返回null")
  }

  @Test
  fun `测试 and 中缀函数 - Pair扩展为Triple`() {
    val pair = "first" to "second"
    val triple = pair and "third"

    log.info("原始Pair: {}", pair)
    log.info("扩展后的Triple: {}", triple)

    assertEquals("first", triple.first, "第一个元素应该正确")
    assertEquals("second", triple.second, "第二个元素应该正确")
    assertEquals("third", triple.third, "第三个元素应该正确")
  }

  @Test
  fun `测试 and 中缀函数 - 不同类型的组合`() {
    val pair = 1 to "string"
    val triple = pair and true

    log.info("混合类型Triple: {}", triple)

    assertEquals(1, triple.first, "第一个元素应该是Int类型")
    assertEquals("string", triple.second, "第二个元素应该是String类型")
    assertEquals(true, triple.third, "第三个元素应该是Boolean类型")
  }

  @Test
  fun `测试集合扩展函数的链式调用`() {
    val initialList = listOf(1, 2, 3, 4, 5)

    val result =
      initialList.isNotEmptyRun {
        log.info("处理非空列表，大小: {}", size)
        this.filter { it % 2 == 0 }.map { it * 2 }.joinToString(",")
      }

    assertNotNull(result, "链式调用应该返回结果")
    assertEquals("4,8", result, "应该正确处理偶数并翻倍")
  }

  @Test
  fun `测试 mutableLockMapOf 方法 - 容量计算验证`() {
    // 测试不同大小的初始容量
    val smallMap = mutableLockMapOf("a" to 1)
    val mediumMap = mutableLockMapOf(*(1..10).map { "key$it" to it }.toTypedArray())
    val largeMap = mutableLockMapOf(*(1..100).map { "key$it" to it }.toTypedArray())

    log.info("小Map大小: {}", smallMap.size)
    log.info("中Map大小: {}", mediumMap.size)
    log.info("大Map大小: {}", largeMap.size)

    assertEquals(1, smallMap.size, "小Map应该包含1个元素")
    assertEquals(10, mediumMap.size, "中Map应该包含10个元素")
    assertEquals(100, largeMap.size, "大Map应该包含100个元素")

    // 验证所有元素都正确插入
    assertTrue((1..10).all { mediumMap["key$it"] == it }, "中Map应该包含所有正确的键值对")
    assertTrue((1..100).all { largeMap["key$it"] == it }, "大Map应该包含所有正确的键值对")
  }
}
