package io.github.truenine.composeserver.data.extract.service.impl

import io.github.truenine.composeserver.holders.ResourceHolder
import io.mockk.every
import io.mockk.mockk
import kotlin.system.measureTimeMillis
import kotlin.test.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.io.ByteArrayResource

class LazyAddressCsvServiceImplTest {
  private lateinit var resourceHolder: ResourceHolder
  private lateinit var service: LazyAddressCsvServiceImpl

  private val testCsvContent =
    """
        110000000000,北京市,1,000000000000
        110100000000,北京市市辖区,2,110000000000
        110101000000,东城区,3,110100000000
    """
      .trimIndent()

  @BeforeEach
  fun setup() {
    resourceHolder = mockk(relaxed = true)

    // 设置默认的mock行为
    val testResource =
      object : ByteArrayResource(testCsvContent.toByteArray()) {
        override fun getFilename(): String = "area_code_2024.csv"
      }

    every { resourceHolder.matchConfigResources("area_code*.csv") } returns listOf(testResource)
    every { resourceHolder.getConfigResource(any()) } returns testResource

    service = LazyAddressCsvServiceImpl(resourceHolder)
  }

  @Test
  fun `测试初始化和默认年份版本`() {
    assertEquals("2024", service.supportedDefaultYearVersion)
    assertTrue(service.supportedYearVersions.contains("2024"))
  }

  @Test
  fun `测试添加和删除支持的年份`() {
    // 测试添加新的年份定义
    val newCsvDefine = LazyAddressCsvServiceImpl.CsvDefine("area_code_2023.csv")
    service.addSupportedYear("2023", newCsvDefine)
    assertTrue(service.supportedYearVersions.contains("2023"))

    // 测试删除年份
    service.removeSupportedYear("2023")
    assertTrue("2023" !in service.supportedYearVersions)
  }

  @Test
  fun `测试查找子区域`() {
    val children = service.fetchChildren("110000", "2024")
    assertNotNull(children)
    assertTrue(children.isNotEmpty())
    assertEquals("1101", children.first().code.code)
    assertEquals("北京市市辖区", children.first().name)
  }

  @Test
  fun `测试递归查找子区域`() {
    val allChildren = service.fetchChildrenRecursive("110000", 3, "2024")
    assertNotNull(allChildren)
    assertTrue(allChildren.size >= 2)
    assertTrue(allChildren.any { it.code.code == "110101" })
  }

  @Test
  fun `测试查找特定区域`() {
    val district = service.fetchDistrict("110101", "2024")
    assertNotNull(district)
    assertEquals("东城区", district.name)
    assertEquals(3, district.level)
  }

  @Test
  fun `测试查找不存在的区域`() {
    val district = service.fetchDistrict("999999", "2024")
    assertNull(district)
  }

  @Test
  fun `测试CSV资源加载`() {
    val resource = service.getCsvResource("2024")
    assertNotNull(resource)

    val sequence = service.getCsvSequence("2024")
    assertNotNull(sequence)
    val districts = sequence.toList()
    assertEquals(3, districts.size)
  }

  @Test
  fun `测试运算符重载功能`() {
    val newCsvDefine = LazyAddressCsvServiceImpl.CsvDefine("area_code_2023.csv")
    service += "2023" to newCsvDefine
    assertTrue(service.supportedYearVersions.contains("2023"))

    service -= "2023"
    assertTrue("2023" !in service.supportedYearVersions)
  }

  @Test
  fun `测试查找国家级子区域`() {
    val children = service.fetchAllProvinces()
    assertNotNull(children)
    assertTrue(children.isNotEmpty())
    assertTrue(children.all { it.level == 1 })
  }

  @Test
  fun `测试无效的区域代码`() {
    val children = service.fetchChildren("invalid", "2024")
    assertTrue(children.isEmpty())

    val district = service.fetchDistrict("invalid", "2024")
    assertNull(district)
  }

  @Test
  fun `测试递归查找时深度限制`() {
    val children = service.fetchChildrenRecursive("110000", 0, "2024")
    assertTrue(children.isEmpty())

    val singleLevel = service.fetchChildrenRecursive("110000", 1, "2024")
    assertTrue(singleLevel.all { it.level == 2 })
  }

  @Test
  fun `测试缓存机制`() {
    // 第一次调用会加载数据
    service.fetchChildren("110000", "2024")

    // 第二次调用应该使用缓存
    val testResource = ByteArrayResource("".toByteArray(), "area_code_2024.csv")
    every { resourceHolder.getConfigResource(any()) } returns testResource

    val children = service.fetchChildren("110000", "2024")
    assertNotNull(children)
    assertTrue(children.isNotEmpty())
  }

  @Test
  fun `测试不存在的年份版本`() {
    val children = service.fetchChildren("110000", "1900")
    assertTrue(children.isEmpty())

    val district = service.fetchDistrict("110000", "1900")
    assertNull(district)
  }

  @Test
  fun `测试CSV格式错误处理`() {
    val invalidCsvContent =
      """
            110000
            110100,北京市
            invalid,data,here
        """
        .trimIndent()

    val invalidResource = ByteArrayResource(invalidCsvContent.toByteArray(), "area_code_2024.csv")
    every { resourceHolder.getConfigResource(any()) } returns invalidResource

    // 优化后的代码会优雅地处理错误，过滤掉无效行
    val result = service.getCsvSequence("2024")?.toList()
    assertNotNull(result)
    // 应该只有有效的行被处理，无效行被过滤掉
    assertTrue(result.isEmpty()) // 因为所有行都是无效的
  }

  @Test
  fun `测试资源不可用情况`() {
    every { resourceHolder.getConfigResource(any()) } returns null

    val resource = service.getCsvResource("2024")
    assertNull(resource)

    val sequence = service.getCsvSequence("2024")
    assertNull(sequence)
  }

  @Test
  fun `traverseChildrenRecursive 正常递归遍历所有子节点`() {
    val visited = mutableListOf<Pair<String, Int>>()
    service.traverseChildrenRecursive("110000", 3, "2024") { children, depth, parent ->
      children.forEach { district -> visited += district.code.code to depth }
      true // 继续递归
    }
    // 应该遍历到所有下级
    assertTrue(visited.any { it.first == "1101" && it.second == 1 }) // 市
    assertTrue(visited.any { it.first == "110101" && it.second == 2 }) // 区
  }

  @Test
  fun `traverseChildrenRecursive 回调返回false时中断分支`() {
    val visited = mutableListOf<String>()
    service.traverseChildrenRecursive("110000", 3, "2024") { children, depth, parent ->
      children.forEach { district -> visited += district.code.code }
      // 只遍历到市级
      children.all { it.level < 2 }
    }
    // 只会访问到省和市，不会访问到区县
    assertTrue(visited.contains("1101"))
    assertFalse(visited.contains("110101"))
  }

  @Test
  fun `traverseChildrenRecursive 只遍历一层`() {
    val visited = mutableListOf<String>()
    service.traverseChildrenRecursive("110000", 1, "2024") { children, depth, parent ->
      children.forEach { district -> visited += district.code.code }
      true
    }
    // 只会访问到市级
    assertEquals(listOf("1101"), visited)
  }

  @Test
  fun `traverseChildrenRecursive parentDistrict 参数正确`() {
    val parentMap = mutableMapOf<String, String?>()
    service.traverseChildrenRecursive("110000", 3, "2024") { children, depth, parent ->
      children.forEach { district -> parentMap[district.code.code] = parent?.code?.code }
      true
    }
    // 市的父是 null，区的父是市
    assertNull(parentMap["1101"])
    assertEquals("1101", parentMap["110101"])
  }

  @Test
  fun `traverseChildrenRecursive 空数据和无效parentCode`() {
    val visited = mutableListOf<String>()
    service.traverseChildrenRecursive("999999", 3, "2024") { children, depth, parent ->
      children.forEach { district -> visited += district.code.code }
      true
    }
    assertTrue(visited.isEmpty())

    val visited2 = mutableListOf<String>()
    service.traverseChildrenRecursive("invalid", 3, "2024") { children, depth, parent ->
      children.forEach { district -> visited2 += district.code.code }
      true
    }
    assertTrue(visited2.isEmpty())
  }

  @Test
  fun `空CSV文件 fetchChildren 返回空`() {
    val emptyResource =
      object : ByteArrayResource("".toByteArray()) {
        override fun getFilename() = "area_code_2024.csv"
      }
    every { resourceHolder.getConfigResource(any()) } returns emptyResource
    val children = service.fetchChildren("110000", "2024")
    assertTrue(children.isEmpty())
  }

  @Test
  fun `只有省级数据 fetchChildrenRecursive 只返回省`() {
    val provinceOnly =
      """
    110000000000,北京市,1,000000000000
  """
        .trimIndent()
    val resource =
      object : ByteArrayResource(provinceOnly.toByteArray()) {
        override fun getFilename() = "area_code_2024.csv"
      }
    every { resourceHolder.getConfigResource(any()) } returns resource
    val result = mutableListOf<String>()
    service.traverseChildrenRecursive("000000000000", 3, "2024") { children, depth, parent ->
      children.forEach { district -> result += district.code.code }
      true
    }
    assertEquals(listOf("11"), result)
  }

  @Test
  fun `CSV脏数据 level非数字 graceful fail`() {
    val badCsv =
      """
      110000000000,北京市,notanumber,000000000000
    """
        .trimIndent()
    val resource =
      object : ByteArrayResource(badCsv.toByteArray()) {
        override fun getFilename() = "area_code_2024.csv"
      }
    every { resourceHolder.getConfigResource(any()) } returns resource

    // 优化后的代码会优雅地处理错误，过滤掉无效行
    val result = service.getCsvSequence("2024")?.toList()
    assertNotNull(result)
    // 无效的数字格式行应该被过滤掉
    assertTrue(result.isEmpty())
  }

  @Test
  fun `parentCode为空 fetchChildren 返回空`() {
    val children = service.fetchChildren("", "2024")
    assertTrue(children.isEmpty())
  }

  @Test
  fun `年份为空 fetchChildren 返回空`() {
    val children = service.fetchChildren("110000", "")
    assertTrue(children.isEmpty())
  }

  @Test
  fun `test optimized fetchChildren performance with indexed lookup`() {
    // Warm up the cache
    service.fetchChildren("110000", "2024")

    val iterations = 1000
    val time = measureTimeMillis { repeat(iterations) { service.fetchChildren("110000", "2024") } }

    // Should be very fast with indexed lookup
    assertTrue(time < 100, "Indexed lookup should be fast: ${time}ms for $iterations operations")
  }

  @Test
  fun `test optimized fetchDistrict performance with indexed lookup`() {
    // Warm up the cache
    service.fetchDistrict("110101", "2024")

    val iterations = 1000
    val time = measureTimeMillis { repeat(iterations) { service.fetchDistrict("110101", "2024") } }

    // Should be very fast with O(1) lookup
    assertTrue(time < 50, "District lookup should be very fast: ${time}ms for $iterations operations")
  }

  @Test
  fun `test cache effectiveness and memory management`() {
    val initialMemory = Runtime.getRuntime().let { it.totalMemory() - it.freeMemory() }

    // Load data multiple times - should use cache after first load
    repeat(10) {
      service.fetchChildren("110000", "2024")
      service.fetchDistrict("110101", "2024")
    }

    System.gc()
    val finalMemory = Runtime.getRuntime().let { it.totalMemory() - it.freeMemory() }
    val memoryIncrease = finalMemory - initialMemory

    // Memory increase should be reasonable
    assertTrue(memoryIncrease < 5 * 1024 * 1024, "Memory usage should be reasonable: ${memoryIncrease / 1024}KB")
  }

  @Test
  fun `test concurrent access to cached data`() {
    // Pre-load cache
    service.fetchChildren("110000", "2024")

    val threadCount = 10
    val results = mutableListOf<Int>()
    val threads =
      (1..threadCount).map {
        Thread {
          val children = service.fetchChildren("110000", "2024")
          synchronized(results) { results.add(children.size) }
        }
      }

    threads.forEach { it.start() }
    threads.forEach { it.join() }

    // All threads should get consistent results
    assertEquals(threadCount, results.size)
    assertTrue(results.all { it == results.first() })
  }

  @Test
  fun `test error handling with malformed CSV data`() {
    val malformedCsv =
      """
      110000000000,北京市,invalid_level,000000000000
      110100000000,北京市市辖区,2,110000000000
    """
        .trimIndent()

    val malformedResource =
      object : ByteArrayResource(malformedCsv.toByteArray()) {
        override fun getFilename() = "area_code_2024.csv"
      }

    every { resourceHolder.getConfigResource(any()) } returns malformedResource

    // Should handle malformed data gracefully
    val children = service.fetchChildren("110000", "2024")
    assertEquals(1, children.size) // Only valid row should be processed
  }

  @Test
  fun `test early validation optimization`() {
    val time = measureTimeMillis {
      repeat(1000) {
        // These should return immediately without processing
        service.fetchChildren("", "2024")
        service.fetchChildren("110000", "")
        service.fetchDistrict("", "2024")
        service.fetchDistrict("110000", "")
      }
    }

    // Early validation should make these very fast
    assertTrue(time < 50, "Early validation should be very fast: ${time}ms")
  }
}
