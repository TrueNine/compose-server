package io.github.truenine.composeserver.consts

import io.github.truenine.composeserver.testtoolkit.log
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * # HTTP 方法常量测试
 *
 * 测试 IMethods 中定义的各种 HTTP 方法常量
 */
class IMethodsTest {

  @Test
  fun `测试 HTTP 方法常量值`() {
    log.info("测试 HTTP 方法常量值")

    assertEquals("GET", IMethods.GET)
    assertEquals("POST", IMethods.POST)
    assertEquals("PUT", IMethods.PUT)
    assertEquals("DELETE", IMethods.DELETE)
    assertEquals("OPTIONS", IMethods.OPTIONS)
    assertEquals("PATCH", IMethods.PATCH)
    assertEquals("HEAD", IMethods.HEAD)
    assertEquals("TRACE", IMethods.TRACE)

    log.info("验证了所有 HTTP 方法常量值")
  }

  @Test
  fun `测试 all 方法返回所有 HTTP 方法`() {
    log.info("测试 all 方法返回所有 HTTP 方法")

    val allMethods = IMethods.all()

    assertEquals(8, allMethods.size, "应该包含 8 个 HTTP 方法")

    val expectedMethods = arrayOf(IMethods.GET, IMethods.POST, IMethods.PUT, IMethods.DELETE, IMethods.OPTIONS, IMethods.PATCH, IMethods.HEAD, IMethods.TRACE)

    expectedMethods.forEach { method -> assertTrue(allMethods.contains(method), "all() 方法应该包含: $method") }

    log.info("all() 方法返回的 HTTP 方法: {}", allMethods.contentToString())
  }

  @Test
  fun `测试 all 方法返回的数组顺序`() {
    log.info("测试 all 方法返回的数组顺序")

    val allMethods = IMethods.all()
    val expectedOrder = arrayOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD", "TRACE")

    assertEquals(expectedOrder.size, allMethods.size, "数组大小应该匹配")

    expectedOrder.forEachIndexed { index, expectedMethod -> assertEquals(expectedMethod, allMethods[index], "索引 $index 处的方法应该是 $expectedMethod") }

    log.info("验证了 all() 方法返回的数组顺序")
  }

  @Test
  fun `测试常用 HTTP 方法`() {
    log.info("测试常用 HTTP 方法")

    val commonMethods = listOf(IMethods.GET, IMethods.POST, IMethods.PUT, IMethods.DELETE)
    val allMethods = IMethods.all()

    commonMethods.forEach { method -> assertTrue(allMethods.contains(method), "常用方法 $method 应该在 all() 中") }

    log.info("验证了常用 HTTP 方法: {}", commonMethods)
  }

  @Test
  fun `测试扩展 HTTP 方法`() {
    log.info("测试扩展 HTTP 方法")

    val extendedMethods = listOf(IMethods.OPTIONS, IMethods.PATCH, IMethods.HEAD, IMethods.TRACE)
    val allMethods = IMethods.all()

    extendedMethods.forEach { method -> assertTrue(allMethods.contains(method), "扩展方法 $method 应该在 all() 中") }

    log.info("验证了扩展 HTTP 方法: {}", extendedMethods)
  }

  @Test
  fun `测试 HTTP 方法常量的大写规范`() {
    log.info("测试 HTTP 方法常量的大写规范")

    val allMethods = IMethods.all()

    allMethods.forEach { method ->
      assertEquals(method.uppercase(), method, "HTTP 方法应该是大写: $method")
      assertTrue(method.all { it.isUpperCase() || !it.isLetter() }, "HTTP 方法应该全部大写: $method")
    }

    log.info("验证了所有 HTTP 方法的大写规范")
  }

  @Test
  fun `测试 HTTP 方法常量的唯一性`() {
    log.info("测试 HTTP 方法常量的唯一性")

    val allMethods = IMethods.all()
    val uniqueMethods = allMethods.toSet()

    assertEquals(allMethods.size, uniqueMethods.size, "所有 HTTP 方法应该是唯一的")

    log.info("验证了 {} 个 HTTP 方法的唯一性", allMethods.size)
  }

  @Test
  fun `测试 HTTP 方法的标准性`() {
    log.info("测试 HTTP 方法的标准性")

    // 验证这些都是标准的 HTTP 方法
    val standardMethods = setOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD", "TRACE")

    val allMethods = IMethods.all().toSet()

    assertEquals(standardMethods, allMethods, "应该只包含标准的 HTTP 方法")

    log.info("验证了所有方法都是标准 HTTP 方法")
  }

  @Test
  fun `测试 all 方法的不可变性`() {
    log.info("测试 all 方法的不可变性")

    val methods1 = IMethods.all()
    val methods2 = IMethods.all()

    // 修改第一个数组不应该影响第二个数组
    methods1[0] = "MODIFIED"

    assertEquals("GET", methods2[0], "修改一个数组不应该影响另一个数组")

    log.info("验证了 all() 方法返回的数组的独立性")
  }
}
