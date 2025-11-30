package io.github.truenine.composeserver.data.extract.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.*

class CnDistrictCodeTest {

  @Test
  fun `empty constructor`() {
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
  fun `invalid length code should throw exception`() {
    val invalidLengths = listOf(1, 3, 5, 7, 8, 10, 11)
    invalidLengths.forEach { length ->
      val invalidCode = "1".repeat(length)
      assertThrows<IllegalArgumentException> { CnDistrictCode(invalidCode) }
    }
  }

  @Test
  fun `province level code`() {
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
  fun `city level code`() {
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
  fun `county level code`() {
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
  fun `town level code`() {
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
  fun `village level code`() {
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
  fun `back method returns correct level for each step`() {
    // Village level returns town level
    val villageCode = CnDistrictCode("110101001001")
    val townCode = villageCode.back()
    assertEquals("110101001", townCode?.code)
    assertEquals(4, townCode?.level)

    // Town level returns county level
    val countyCode = townCode?.back()
    assertEquals("110101", countyCode?.code)
    assertEquals(3, countyCode?.level)

    // County level returns city level
    val cityCode = countyCode?.back()
    assertEquals("1101", cityCode?.code)
    assertEquals(2, cityCode?.level)

    // City level returns province level
    val provinceCode = cityCode?.back()
    assertEquals("11", provinceCode?.code)
    assertEquals(1, provinceCode?.level)

    // Province level returns empty code
    val emptyCode = provinceCode?.back()
    assertEquals("", emptyCode?.code)
    assertEquals(0, emptyCode?.level)

    // Empty code returns null
    val nullCode = emptyCode?.back()
    assertNull(nullCode)
  }

  @Test
  fun `special code cases`() {
    // All-zero code
    val zeroCode = CnDistrictCode("000000000000")
    assertTrue(zeroCode.empty)
    assertEquals(0, zeroCode.level)

    // Partially zero code
    val partialZeroCode = CnDistrictCode("110000000000")
    assertFalse(partialZeroCode.empty)
    assertEquals(1, partialZeroCode.level)
  }

  @Test
  fun `code padding`() {
    val codes =
      mapOf("11" to "110000000000", "1101" to "110100000000", "110101" to "110101000000", "110101001" to "110101001000", "110101001001" to "110101001001")

    codes.forEach { (input, expected) ->
      val districtCode = CnDistrictCode(input)
      assertEquals(expected, districtCode.toString())
      assertEquals(expected, districtCode.padCode)
    }
  }

  @Test
  fun `level calculation logic`() {
    val testCases =
      mapOf(
        "000000000000" to 0, // Empty code
        "110000000000" to 1, // Province level
        "110100000000" to 2, // City level
        "110101000000" to 3, // County level
        "110101001000" to 4, // Town level
        "110101001001" to 5, // Village level
      )

    testCases.forEach { (input, expectedLevel) ->
      val districtCode = CnDistrictCode(input)
      assertEquals(expectedLevel, districtCode.level, "input code: $input")
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
