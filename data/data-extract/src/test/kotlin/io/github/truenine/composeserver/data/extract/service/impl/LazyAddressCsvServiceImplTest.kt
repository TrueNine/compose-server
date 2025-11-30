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
    110000000000,Beijing Municipality,1,000000000000
    110100000000,Beijing Districts,2,110000000000
    110101000000,Dongcheng District,3,110100000000
    """
      .trimIndent()

  @BeforeEach
  fun setup() {
    resourceHolder = mockk(relaxed = true)

    // Set up default mock behavior
    val testResource =
      object : ByteArrayResource(testCsvContent.toByteArray()) {
        override fun getFilename(): String = "area_code_2024.csv"
      }

    every { resourceHolder.matchConfigResources("area_code*.csv") } returns listOf(testResource)
    every { resourceHolder.getConfigResource(any()) } returns testResource

    service = LazyAddressCsvServiceImpl(resourceHolder)
  }

  @Test
  fun `initialize and default year version`() {
    assertEquals("2024", service.supportedDefaultYearVersion)
    assertTrue(service.supportedYearVersions.contains("2024"))
  }

  @Test
  fun `add and remove supported year`() {
    // Test adding new year definition
    val newCsvDefine = LazyAddressCsvServiceImpl.CsvDefine("area_code_2023.csv")
    service.addSupportedYear("2023", newCsvDefine)
    assertTrue(service.supportedYearVersions.contains("2023"))

    // Test removing year definition
    service.removeSupportedYear("2023")
    assertTrue("2023" !in service.supportedYearVersions)
  }

  @Test
  fun `fetchChildren finds child regions`() {
    val children = service.fetchChildren("110000", "2024")
    assertNotNull(children)
    assertTrue(children.isNotEmpty())
    assertEquals("1101", children.first().code.code)
    assertEquals("Beijing Districts", children.first().name)
  }

  @Test
  fun `fetchChildrenRecursive finds child regions`() {
    val allChildren = service.fetchChildrenRecursive("110000", 3, "2024")
    assertNotNull(allChildren)
    assertTrue(allChildren.size >= 2)
    assertTrue(allChildren.any { it.code.code == "110101" })
  }

  @Test
  fun `fetchDistrict finds specific region`() {
    val district = service.fetchDistrict("110101", "2024")
    assertNotNull(district)
    assertEquals("Dongcheng District", district.name)
    assertEquals(3, district.level)
  }

  @Test
  fun `fetchDistrict for non-existing region`() {
    val district = service.fetchDistrict("999999", "2024")
    assertNull(district)
  }

  @Test
  fun `csv resource loading`() {
    val resource = service.getCsvResource("2024")
    assertNotNull(resource)

    val sequence = service.getCsvSequence("2024")
    assertNotNull(sequence)
    val districts = sequence.toList()
    assertEquals(3, districts.size)
  }

  @Test
  fun `operator overloading for year mapping`() {
    val newCsvDefine = LazyAddressCsvServiceImpl.CsvDefine("area_code_2023.csv")
    service += "2023" to newCsvDefine
    assertTrue(service.supportedYearVersions.contains("2023"))

    service -= "2023"
    assertTrue("2023" !in service.supportedYearVersions)
  }

  @Test
  fun `fetchAllProvinces finds national children`() {
    val children = service.fetchAllProvinces()
    assertNotNull(children)
    assertTrue(children.isNotEmpty())
    assertTrue(children.all { it.level == 1 })
  }

  @Test
  fun `invalid region code`() {
    val children = service.fetchChildren("invalid", "2024")
    assertTrue(children.isEmpty())

    val district = service.fetchDistrict("invalid", "2024")
    assertNull(district)
  }

  @Test
  fun `fetchChildrenRecursive respects depth limit`() {
    val children = service.fetchChildrenRecursive("110000", 0, "2024")
    assertTrue(children.isEmpty())

    val singleLevel = service.fetchChildrenRecursive("110000", 1, "2024")
    assertTrue(singleLevel.all { it.level == 2 })
  }

  @Test
  fun `cache mechanism`() {
    // First call loads data
    service.fetchChildren("110000", "2024")

    // Second call should use cache
    val testResource = ByteArrayResource("".toByteArray(), "area_code_2024.csv")
    every { resourceHolder.getConfigResource(any()) } returns testResource

    val children = service.fetchChildren("110000", "2024")
    assertNotNull(children)
    assertTrue(children.isNotEmpty())
  }

  @Test
  fun `nonexistent year version`() {
    val children = service.fetchChildren("110000", "1900")
    assertTrue(children.isEmpty())

    val district = service.fetchDistrict("110000", "1900")
    assertNull(district)
  }

  @Test
  fun `invalid CSV format is handled gracefully`() {
    val invalidCsvContent =
      """
      110000
      110100,Beijing City
      invalid,data,here
      """
        .trimIndent()

    val invalidResource = ByteArrayResource(invalidCsvContent.toByteArray(), "area_code_2024.csv")
    every { resourceHolder.getConfigResource(any()) } returns invalidResource

    // Optimized code should handle errors gracefully and filter invalid rows
    val result = service.getCsvSequence("2024")?.toList()
    assertNotNull(result)
    // Only valid rows should be processed, invalid rows should be filtered out
    assertTrue(result.isEmpty()) // All rows are invalid in this dataset
  }

  @Test
  fun `unavailable resource`() {
    every { resourceHolder.getConfigResource(any()) } returns null

    val resource = service.getCsvResource("2024")
    assertNull(resource)

    val sequence = service.getCsvSequence("2024")
    assertNull(sequence)
  }

  @Test
  fun `traverseChildrenRecursive traverses all child nodes`() {
    val visited = mutableListOf<Pair<String, Int>>()
    service.traverseChildrenRecursive("110000", 3, "2024") { children, depth, parent ->
      children.forEach { district -> visited += district.code.code to depth }
      true // Continue recursion
    }
    // Should traverse all lower-level regions
    assertTrue(visited.any { it.first == "1101" && it.second == 1 }) // City level
    assertTrue(visited.any { it.first == "110101" && it.second == 2 }) // District level
  }

  @Test
  fun `traverseChildrenRecursive stops branch when callback returns false`() {
    val visited = mutableListOf<String>()
    service.traverseChildrenRecursive("110000", 3, "2024") { children, depth, parent ->
      children.forEach { district -> visited += district.code.code }
      // Traverse only to city level
      children.all { it.level < 2 }
    }
    // Should visit provinces and cities only, not counties
    assertTrue(visited.contains("1101"))
    assertFalse(visited.contains("110101"))
  }

  @Test
  fun `traverseChildrenRecursive single level`() {
    val visited = mutableListOf<String>()
    service.traverseChildrenRecursive("110000", 1, "2024") { children, depth, parent ->
      children.forEach { district -> visited += district.code.code }
      true
    }
    // Only city level should be visited
    assertEquals(listOf("1101"), visited)
  }

  @Test
  fun `traverseChildrenRecursive parentDistrict parameter is correct`() {
    val parentMap = mutableMapOf<String, String?>()
    service.traverseChildrenRecursive("110000", 3, "2024") { children, depth, parent ->
      children.forEach { district -> parentMap[district.code.code] = parent?.code?.code }
      true
    }
    // City parent is null, district parent is the city
    assertNull(parentMap["1101"])
    assertEquals("1101", parentMap["110101"])
  }

  @Test
  fun `traverseChildrenRecursive empty data and invalid parentCode`() {
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
  fun `empty CSV fetchChildren returns empty`() {
    val emptyResource =
      object : ByteArrayResource("".toByteArray()) {
        override fun getFilename() = "area_code_2024.csv"
      }
    every { resourceHolder.getConfigResource(any()) } returns emptyResource
    val children = service.fetchChildren("110000", "2024")
    assertTrue(children.isEmpty())
  }

  @Test
  fun `province-only data fetchChildrenRecursive returns only provinces`() {
    val provinceOnly =
      """
      110000000000,Beijing Municipality,1,000000000000
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
  fun `CSV dirty data non-numeric level graceful fail`() {
    val badCsv =
      """
      110000000000,Beijing Municipality,notanumber,000000000000
      """
        .trimIndent()
    val resource =
      object : ByteArrayResource(badCsv.toByteArray()) {
        override fun getFilename() = "area_code_2024.csv"
      }
    every { resourceHolder.getConfigResource(any()) } returns resource

    // Optimized code should handle errors gracefully and filter invalid rows
    val result = service.getCsvSequence("2024")?.toList()
    assertNotNull(result)
    // Rows with invalid numeric formats should be filtered out
    assertTrue(result.isEmpty())
  }

  @Test
  fun `empty parentCode fetchChildren returns empty`() {
    val children = service.fetchChildren("", "2024")
    assertTrue(children.isEmpty())
  }

  @Test
  fun `empty year fetchChildren returns empty`() {
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
      110000000000,Beijing Municipality,invalid_level,000000000000
      110100000000,Beijing Districts,2,110000000000
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
