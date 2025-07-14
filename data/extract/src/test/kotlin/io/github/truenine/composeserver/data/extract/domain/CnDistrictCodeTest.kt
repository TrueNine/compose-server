package io.github.truenine.composeserver.data.extract.domain

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CnDistrictCodeTest {

  @Test
  fun `测试空构造函数`() {
    val code = CnDistrictCode()
    assertEquals("000000000000", code.toString())
    assertTrue(code.empty)
    assertEquals(0, code.level)
    assertEquals("00", code.provinceCode)
    assertEquals("00", code.cityCode)
    assertEquals("00", code.countyCode)
    assertEquals("000", code.townCode)
    assertEquals("000", code.villageCode)
  }

  @Test
  fun `测试无效长度的编码应该抛出异常`() {
    val invalidLengths = listOf(1, 3, 5, 7, 8, 10, 11)
    invalidLengths.forEach { length ->
      val invalidCode = "1".repeat(length)
      assertThrows<IllegalArgumentException> { CnDistrictCode(invalidCode) }
    }
  }

  @Test
  fun `测试省级编码`() {
    val code = CnDistrictCode("11")
    assertEquals("110000000000", code.toString())
    assertFalse(code.empty)
    assertEquals(1, code.level)
    assertEquals("11", code.provinceCode)
    assertEquals("00", code.cityCode)
    assertEquals("00", code.countyCode)
    assertEquals("000", code.townCode)
    assertEquals("000", code.villageCode)
    assertEquals("11", code.code)
  }

  @Test
  fun `测试市级编码`() {
    val code = CnDistrictCode("1101")
    assertEquals("110100000000", code.toString())
    assertFalse(code.empty)
    assertEquals(2, code.level)
    assertEquals("11", code.provinceCode)
    assertEquals("01", code.cityCode)
    assertEquals("00", code.countyCode)
    assertEquals("000", code.townCode)
    assertEquals("000", code.villageCode)
    assertEquals("1101", code.code)
  }

  @Test
  fun `测试区县级编码`() {
    val code = CnDistrictCode("110101")
    assertEquals("110101000000", code.toString())
    assertFalse(code.empty)
    assertEquals(3, code.level)
    assertEquals("11", code.provinceCode)
    assertEquals("01", code.cityCode)
    assertEquals("01", code.countyCode)
    assertEquals("000", code.townCode)
    assertEquals("000", code.villageCode)
    assertEquals("110101", code.code)
  }

  @Test
  fun `测试乡镇级编码`() {
    val code = CnDistrictCode("110101001")
    assertEquals("110101001000", code.toString())
    assertFalse(code.empty)
    assertEquals(4, code.level)
    assertEquals("11", code.provinceCode)
    assertEquals("01", code.cityCode)
    assertEquals("01", code.countyCode)
    assertEquals("001", code.townCode)
    assertEquals("000", code.villageCode)
    assertEquals("110101001", code.code)
  }

  @Test
  fun `测试村级编码`() {
    val code = CnDistrictCode("110101001001")
    assertEquals("110101001001", code.toString())
    assertFalse(code.empty)
    assertEquals(5, code.level)
    assertEquals("11", code.provinceCode)
    assertEquals("01", code.cityCode)
    assertEquals("01", code.countyCode)
    assertEquals("001", code.townCode)
    assertEquals("001", code.villageCode)
    assertEquals("110101001001", code.code)
  }

  @Test
  fun `测试back方法 - 各级别返回值`() {
    // 村级返回乡镇级
    val villageCode = CnDistrictCode("110101001001")
    val townCode = villageCode.back()
    assertEquals("110101001", townCode?.code)
    assertEquals(4, townCode?.level)

    // 乡镇级返回区县级
    val countyCode = townCode?.back()
    assertEquals("110101", countyCode?.code)
    assertEquals(3, countyCode?.level)

    // 区县级返回市级
    val cityCode = countyCode?.back()
    assertEquals("1101", cityCode?.code)
    assertEquals(2, cityCode?.level)

    // 市级返回省级
    val provinceCode = cityCode?.back()
    assertEquals("11", provinceCode?.code)
    assertEquals(1, provinceCode?.level)

    // 省级返回空编码
    val emptyCode = provinceCode?.back()
    assertEquals("", emptyCode?.code)
    assertEquals(0, emptyCode?.level)

    // 空编码返回null
    val nullCode = emptyCode?.back()
    assertNull(nullCode)
  }

  @Test
  fun `测试特殊编码情况`() {
    // 测试全零编码
    val zeroCode = CnDistrictCode("000000000000")
    assertTrue(zeroCode.empty)
    assertEquals(0, zeroCode.level)

    // 测试部分零编码
    val partialZeroCode = CnDistrictCode("110000000000")
    assertFalse(partialZeroCode.empty)
    assertEquals(1, partialZeroCode.level)
  }

  @Test
  fun `测试编码补全`() {
    val codes =
      mapOf("11" to "110000000000", "1101" to "110100000000", "110101" to "110101000000", "110101001" to "110101001000", "110101001001" to "110101001001")

    codes.forEach { (input, expected) ->
      val districtCode = CnDistrictCode(input)
      assertEquals(expected, districtCode.toString())
      assertEquals(expected, districtCode.padCode)
    }
  }

  @Test
  fun `测试level计算逻辑`() {
    val testCases =
      mapOf(
        "000000000000" to 0, // 空编码
        "110000000000" to 1, // 省级
        "110100000000" to 2, // 市级
        "110101000000" to 3, // 区县级
        "110101001000" to 4, // 乡镇级
        "110101001001" to 5, // 村级
      )

    testCases.forEach { (input, expectedLevel) ->
      val districtCode = CnDistrictCode(input)
      assertEquals(expectedLevel, districtCode.level, "输入编码: $input")
    }
  }

  @Test
  fun `test performance optimization - lazy level calculation`() {
    val code = CnDistrictCode("110101001001")

    // First access should calculate and cache the level
    val level1 = code.level
    val level2 = code.level
    val level3 = code.level

    // All accesses should return the same value
    assertEquals(level1, level2)
    assertEquals(level2, level3)
    assertEquals(5, level1)
  }

  @Test
  fun `test performance optimization - string building efficiency`() {
    val iterations = 10000
    val testCodes = listOf("11", "1101", "110101", "110101001", "110101001001")

    val totalTime = kotlin.system.measureTimeMillis { repeat(iterations) { testCodes.forEach { code -> CnDistrictCode(code) } } }

    // Performance should be reasonable for large number of operations
    assertTrue(totalTime < 5000, "String building optimization should complete $iterations iterations in < 5s, took ${totalTime}ms")
  }

  @Test
  fun `test memory efficiency with large batch creation`() {
    val batchSize = 1000
    val codes = mutableListOf<CnDistrictCode>()

    val memoryBefore = Runtime.getRuntime().let { it.totalMemory() - it.freeMemory() }

    repeat(batchSize) { index ->
      val code = String.format("11%04d%06d", index % 10000, index % 1000000)
      codes.add(CnDistrictCode(code))
    }

    System.gc()
    val memoryAfter = Runtime.getRuntime().let { it.totalMemory() - it.freeMemory() }
    val memoryUsed = memoryAfter - memoryBefore

    // Memory usage should be reasonable (less than 10MB for 1000 objects)
    assertTrue(memoryUsed < 10 * 1024 * 1024, "Memory usage should be reasonable: ${memoryUsed / 1024}KB")
    assertEquals(batchSize, codes.size)
  }

  @Test
  fun `test concurrent access safety`() {
    val code = CnDistrictCode("110101001001")
    val threadCount = 10
    val results = mutableListOf<Int>()

    val threads = (1..threadCount).map { Thread { synchronized(results) { results.add(code.level) } } }

    threads.forEach { it.start() }
    threads.forEach { it.join() }

    // All threads should get the same result
    assertEquals(threadCount, results.size)
    assertTrue(results.all { it == 5 })
  }
}
