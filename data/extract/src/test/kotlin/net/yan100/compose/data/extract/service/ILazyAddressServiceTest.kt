package net.yan100.compose.data.extract.service

import net.yan100.compose.string
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * # ILazyAddressService 接口测试
 * > 测试接口伴生对象和默认方法的逻辑
 */
class ILazyAddressServiceTest {

  private lateinit var service: ILazyAddressService

  @BeforeTest
  fun setup() {
    // 使用匿名对象实例来测试默认方法，避免 MockK 对默认实现的影响
    service = object : ILazyAddressService {
      override val supportedYearVersions: List<String> = listOf("2024", "2023", "2021")
      override val supportedDefaultYearVersion: String = "2024"

      // 默认方法测试不依赖这些，提供空实现即可
      override fun fetchChildren(parentCode: String, yearVersion: String): List<ILazyAddressService.CnDistrict> =
        emptyList()

      override fun fetchDistrict(code: String, yearVersion: String): ILazyAddressService.CnDistrict? = null
      override fun fetchChildrenRecursive(
        parentCode: String,
        maxDepth: Int,
        yearVersion: String,
      ): List<ILazyAddressService.CnDistrict> = emptyList()

      override fun traverseChildrenRecursive(
        parentCode: string,
        maxDepth: Int,
        yearVersion: String,
        onVisit: (ILazyAddressService.CnDistrict, Int, ILazyAddressService.CnDistrict?) -> Boolean,
      ) {
        TODO("Not yet implemented")
      }
    }
  }

  // --- Companion Object Tests ---

  @Test
  fun `verifyCode 校验 有效的12位代码 返回 true`() {
    assertTrue(ILazyAddressService.verifyCode("110101001001"))
  }

  @Test
  fun `verifyCode 校验 有效的省级代码 返回 true`() {
    assertTrue(ILazyAddressService.verifyCode("110000000000"))
  }

  @Test
  fun `verifyCode 校验 有效的市级代码 返回 true`() {
    assertTrue(ILazyAddressService.verifyCode("110100000000"))
  }

  @Test
  fun `verifyCode 校验 有效的县级代码 返回 true`() {
    assertTrue(ILazyAddressService.verifyCode("110101000000"))
  }

  @Test
  fun `verifyCode 校验 有效的乡级代码 返回 true`() {
    assertTrue(ILazyAddressService.verifyCode("110101001000"))
  }

  @Test
  fun `verifyCode 校验 长度超过12位 返回 false`() {
    assertFalse(ILazyAddressService.verifyCode("1101010010011"))
  }

  @Test
  fun `verifyCode 校验 包含非数字字符 返回 false`() {
    assertFalse(ILazyAddressService.verifyCode("11010100100A"))
  }

  @Test
  fun `convertToFillCode 处理 有效的短代码 返回 补全后的12位代码`() {
    assertEquals("110000000000", ILazyAddressService.convertToFillCode("11"))
    assertEquals("110100000000", ILazyAddressService.convertToFillCode("1101"))
    assertEquals("110101000000", ILazyAddressService.convertToFillCode("110101"))
    assertEquals("110101001000", ILazyAddressService.convertToFillCode("110101001"))
    assertEquals("110101001001", ILazyAddressService.convertToFillCode("110101001001"))
  }

  @Test
  fun `convertToFillCode 处理 无效的代码 返回 原始字符串`() {
    assertEquals("11010A", ILazyAddressService.convertToFillCode("11010A"))
    assertEquals("", ILazyAddressService.convertToFillCode(""))
    assertEquals("", ILazyAddressService.convertToFillCode(""))
  }

  @Test
  fun `convertToFillCode 处理 已经是12位的代码 返回 原始字符串`() {
    assertEquals("110101001001", ILazyAddressService.convertToFillCode("110101001001"))
  }

  @Test
  fun `createCnDistrictCode 使用 有效的完整代码 返回 CnDistrictCode实例`() {
    val code = "110101001001"
    val districtCode = ILazyAddressService.createCnDistrictCode(code)
    assertNotNull(districtCode)
    assertEquals(code, districtCode.code)
    assertEquals(5, districtCode.level)
  }

  @Test
  fun `createCnDistrictCode 使用 有效的省级代码 返回 CnDistrictCode实例`() {
    val inputCode = "110000000000"
    val expectedMinimalCode = "11" // 假设 CnDistrictCode.code 返回简化代码
    val districtCode = ILazyAddressService.createCnDistrictCode(inputCode)
    assertNotNull(districtCode)
    assertEquals(expectedMinimalCode, districtCode.code) // 校验简化后的 code
    assertEquals(1, districtCode.level)
  }

  @Test
  fun `createCnDistrictCode 使用 有效的短代码 返回 CnDistrictCode实例`() {
    val code = "1101"
    val districtCode = ILazyAddressService.createCnDistrictCode(code)
    assertNotNull(districtCode)
    assertEquals(code, districtCode.code) // 短代码本身就是简化形式
    assertEquals(2, districtCode.level) // level 根据实际长度计算
  }

  @Test
  fun `createCnDistrictCode 使用 无效的代码 返回 null`() {
    assertNull(ILazyAddressService.createCnDistrictCode("11010A"))
    assertNull(ILazyAddressService.createCnDistrictCode("1234567890123")) // 超长
    assertNull(ILazyAddressService.createCnDistrictCode(""))
    assertNull(ILazyAddressService.createCnDistrictCode(null))
  }

  // --- Default Method Tests ---

  @Test
  fun `lastYearVersionOrNull 给定 有效年份 且 存在更早版本 返回 最近的更早版本`() {
    assertEquals("2023", service.lastYearVersionOrNull("2024"))
    assertEquals("2021", service.lastYearVersionOrNull("2023"))
  }

  @Test
  fun `lastYearVersionOrNull 给定 最早年份 返回 null`() {
    assertNull(service.lastYearVersionOrNull("2021"))
  }

  @Test
  fun `lastYearVersionOrNull 给定 不在支持列表中的年份 返回 最近的更早版本`() {
    assertEquals("2021", service.lastYearVersionOrNull("2022")) // 假设支持 ["2024", "2023", "2021"]
  }

  @Test
  fun `lastYearVersionOrNull 给定 无效年份字符串 返回 null`() {
    assertNull(service.lastYearVersionOrNull("abc"))
    assertNull(service.lastYearVersionOrNull(""))
  }

  @Test
  fun `lastYearVersion 返回 支持列表中的最新年份`() {
    assertEquals("2024", service.lastYearVersion)
  }

  @Test
  fun `lastYearVersion 当支持列表为空时 返回 默认年份`() {
    val emptyService = object : ILazyAddressService {
      override val supportedYearVersions: List<String> = emptyList()
      override val supportedDefaultYearVersion: String = "2020" // 假设默认是 2020
      override fun fetchChildren(parentCode: String, yearVersion: String): List<ILazyAddressService.CnDistrict> =
        emptyList()

      override fun fetchDistrict(code: String, yearVersion: String): ILazyAddressService.CnDistrict? = null
      override fun fetchChildrenRecursive(
        parentCode: String,
        maxDepth: Int,
        yearVersion: String,
      ): List<ILazyAddressService.CnDistrict> = emptyList()

      override fun traverseChildrenRecursive(
        parentCode: string,
        maxDepth: Int,
        yearVersion: String,
        onVisit: (ILazyAddressService.CnDistrict, Int, ILazyAddressService.CnDistrict?) -> Boolean,
      ) {
        TODO("Not yet implemented")
      }
    }
    assertEquals("2020", emptyService.lastYearVersion)
  }

  @Test
  fun `supportedMaxLevel 默认 返回 5`() {
    assertEquals(5, service.supportedMaxLevel) // 测试默认实现
  }
} 
