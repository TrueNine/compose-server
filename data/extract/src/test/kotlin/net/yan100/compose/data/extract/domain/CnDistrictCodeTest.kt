package net.yan100.compose.data.extract.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
      mapOf(
        "11" to "110000000000",
        "1101" to "110100000000",
        "110101" to "110101000000",
        "110101001" to "110101001000",
        "110101001001" to "110101001001",
      )

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
}
