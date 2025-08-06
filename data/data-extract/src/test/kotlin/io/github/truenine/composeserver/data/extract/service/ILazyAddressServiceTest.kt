package io.github.truenine.composeserver.data.extract.service

import io.github.truenine.composeserver.data.extract.service.impl.LazyAddressCsvServiceImpl
import io.github.truenine.composeserver.holders.ResourceHolder
import io.github.truenine.composeserver.string
import io.mockk.every
import io.mockk.mockk
import kotlin.system.measureTimeMillis
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.io.ByteArrayResource

/**
 * Comprehensive test suite for ILazyAddressService interface.
 *
 * Tests focus on the backward/reverse retrieval mechanism and comprehensive coverage of all interface functionality including edge cases and error handling.
 *
 * Implementation uses TDD principles with independent, atomic tests targeting the lazyCsv implementation class for integration testing.
 */
class ILazyAddressServiceTest {

  private lateinit var service: ILazyAddressService
  private lateinit var resourceHolder: ResourceHolder
  private lateinit var implementationService: LazyAddressCsvServiceImpl

  /** Comprehensive test data covering multiple administrative levels and various hierarchical relationships for testing backward traversal */
  private val comprehensiveTestData =
    """
    110000000000,北京市,1,000000000000
    120000000000,天津市,1,000000000000
    130000000000,河北省,1,000000000000
    110100000000,北京市市辖区,2,110000000000
    110200000000,北京市县,2,110000000000
    120100000000,天津市市辖区,2,120000000000
    130100000000,石家庄市,2,130000000000
    130200000000,唐山市,2,130000000000
    110101000000,东城区,3,110100000000
    110102000000,西城区,3,110100000000
    110105000000,朝阳区,3,110100000000
    110221000000,昌平区,3,110200000000
    120101000000,和平区,3,120100000000
    120102000000,河东区,3,120100000000
    130102000000,长安区,3,130100000000
    130104000000,桥西区,3,130100000000
    130203000000,古冶区,3,130200000000
    110101001000,东华门街道,4,110101000000
    110101002000,景山街道,4,110101000000
    110101003000,交道口街道,4,110101000000
    110102004000,什刹海街道,4,110102000000
    110105005000,建国门外街道,4,110105000000
    110221006000,城北街道,4,110221000000
    120101007000,劝业场街道,4,120101000000
    120102008000,大王庄街道,4,120102000000
    130102009000,青园街道,4,130102000000
    130104010000,振头街道,4,130104000000
    130203011000,林西街道,4,130203000000
    110101001001,东华门社区,5,110101001000
    110101001002,多福巷社区,5,110101001000
    110101002003,故宫社区,5,110101002000
    110101003004,府学胡同社区,5,110101003000
    110102004005,德胜门社区,5,110102004000
    110105005006,建外街道社区,5,110105005000
    110221006007,城北第一社区,5,110221006000
    120101007008,劝业场社区,5,120101007000
    120102008009,大王庄第一社区,5,120102008000
    130102009010,青园第一社区,5,130102009000
    130104010011,振头第一社区,5,130104010000
    130203011012,林西第一社区,5,130203011000
  """
      .trimIndent()

  @BeforeTest
  fun setup() {
    // Setup for interface companion object testing with anonymous implementation
    service =
      object : ILazyAddressService {
        override val supportedYearVersions: List<String> = listOf("2024", "2023", "2021")
        override val supportedDefaultYearVersion: String = "2024"

        override fun fetchChildren(parentCode: String, yearVersion: String): List<ILazyAddressService.CnDistrict> = emptyList()

        override fun fetchDistrict(code: String, yearVersion: String): ILazyAddressService.CnDistrict? = null

        override fun fetchChildrenRecursive(parentCode: String, maxDepth: Int, yearVersion: String): List<ILazyAddressService.CnDistrict> = emptyList()

        override fun traverseChildrenRecursive(
          parentCode: string,
          maxDepth: Int,
          yearVersion: String,
          onVisit: (List<ILazyAddressService.CnDistrict>, Int, ILazyAddressService.CnDistrict?) -> Boolean,
        ) {}
      }
  }

  @BeforeEach
  fun setupImplementationTests() {
    // Setup for comprehensive implementation testing
    resourceHolder = mockk(relaxed = true)

    val testResource =
      object : ByteArrayResource(comprehensiveTestData.toByteArray()) {
        override fun getFilename(): String = "area_code_2024.csv"
      }

    every { resourceHolder.matchConfigResources("area_code*.csv") } returns listOf(testResource)
    every { resourceHolder.getConfigResource(any()) } returns testResource

    implementationService = LazyAddressCsvServiceImpl(resourceHolder)
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
    val emptyService =
      object : ILazyAddressService {
        override val supportedYearVersions: List<String> = emptyList()
        override val supportedDefaultYearVersion: String = "2020" // 假设默认是 2020

        override fun fetchChildren(parentCode: String, yearVersion: String): List<ILazyAddressService.CnDistrict> = emptyList()

        override fun fetchDistrict(code: String, yearVersion: String): ILazyAddressService.CnDistrict? = null

        override fun fetchChildrenRecursive(parentCode: String, maxDepth: Int, yearVersion: String): List<ILazyAddressService.CnDistrict> = emptyList()

        override fun traverseChildrenRecursive(
          parentCode: string,
          maxDepth: Int,
          yearVersion: String,
          onVisit: (List<ILazyAddressService.CnDistrict>, Int, ILazyAddressService.CnDistrict?) -> Boolean,
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

  // === Core Interface Implementation Tests ===

  @Test
  fun `fetchChildren returns direct children correctly`() {
    val provinces = implementationService.fetchChildren("000000000000", "2024")
    assertEquals(3, provinces.size)
    assertTrue(provinces.any { it.name == "北京市" })
    assertTrue(provinces.any { it.name == "天津市" })
    assertTrue(provinces.any { it.name == "河北省" })
    assertTrue(provinces.all { it.level == 1 })
  }

  @Test
  fun `fetchChildren handles country code with default constant`() {
    val childrenWithDefaultCode = implementationService.fetchChildren(ILazyAddressService.DEFAULT_COUNTRY_CODE, "2024")
    val childrenWithZeroCode = implementationService.fetchChildren("000000000000", "2024")
    assertEquals(childrenWithDefaultCode.size, childrenWithZeroCode.size)
    assertEquals(childrenWithDefaultCode.map { it.code.code }.sorted(), childrenWithZeroCode.map { it.code.code }.sorted())
  }

  @Test
  fun `fetchAllProvinces returns all province level districts`() {
    val provinces = implementationService.fetchAllProvinces("2024")
    assertEquals(3, provinces.size)
    assertTrue(provinces.all { it.level == 1 })
    assertTrue(provinces.any { it.code.code == "11" })
    assertTrue(provinces.any { it.code.code == "12" })
    assertTrue(provinces.any { it.code.code == "13" })
  }

  @Test
  fun `fetchDistrict finds specific district correctly`() {
    val district = implementationService.fetchDistrict("110101", "2024")
    assertNotNull(district)
    assertEquals("东城区", district.name)
    assertEquals(3, district.level)
    assertEquals("110101", district.code.code)
  }

  @Test
  fun `fetchDistrict handles code padding correctly`() {
    val districtShort = implementationService.fetchDistrict("110101", "2024")
    val districtFull = implementationService.fetchDistrict("110101000000", "2024")

    assertNotNull(districtShort)
    assertNotNull(districtFull)
    assertEquals("东城区", districtShort.name)
    assertEquals("东城区", districtFull.name)
    assertEquals(districtShort.level, districtFull.level)
  }

  // === Backward/Reverse Retrieval Mechanism Tests ===

  @Test
  fun `backward traversal from village to province works correctly`() {
    val village = implementationService.fetchDistrict("110101001001", "2024")
    assertNotNull(village)
    assertEquals(5, village.level)
    assertEquals("东华门社区", village.name)

    val parentTown = village.code.back()
    assertNotNull(parentTown)
    val town = implementationService.fetchDistrict(parentTown.code, "2024")
    assertNotNull(town)
    assertEquals(4, town.level)
    assertEquals("东华门街道", town.name)

    val parentCounty = town.code.back()
    assertNotNull(parentCounty)
    val county = implementationService.fetchDistrict(parentCounty.code, "2024")
    assertNotNull(county)
    assertEquals(3, county.level)
    assertEquals("东城区", county.name)

    val parentCity = county.code.back()
    assertNotNull(parentCity)
    val city = implementationService.fetchDistrict(parentCity.code, "2024")
    assertNotNull(city)
    assertEquals(2, city.level)
    assertEquals("北京市市辖区", city.name)

    val parentProvince = city.code.back()
    assertNotNull(parentProvince)
    val province = implementationService.fetchDistrict(parentProvince.code, "2024")
    assertNotNull(province)
    assertEquals(1, province.level)
    assertEquals("北京市", province.name)

    val parentCountry = province.code.back()
    assertNotNull(parentCountry)
    assertTrue(parentCountry.empty)
  }

  @Test
  fun `backward traversal validates parent-child relationships`() {
    val village1 = implementationService.fetchDistrict("110101001001", "2024")
    val village2 = implementationService.fetchDistrict("110101001002", "2024")

    assertNotNull(village1)
    assertNotNull(village2)

    val parent1 = village1.code.back()
    val parent2 = village2.code.back()
    assertEquals(parent1?.code, parent2?.code)

    val townChildren = implementationService.fetchChildren(parent1!!.code, "2024")
    assertTrue(townChildren.any { it.code.code == village1.code.code })
    assertTrue(townChildren.any { it.code.code == village2.code.code })
  }

  @Test
  fun `backward traversal from different levels produces correct hierarchy`() {
    val testCodes =
      listOf(
        "110101001001" to 5, // Village
        "110101001000" to 4, // Town
        "110101000000" to 3, // County
        "110100000000" to 2, // City
        "110000000000" to 1, // Province
      )

    testCodes.forEach { (code, expectedLevel) ->
      val district = implementationService.fetchDistrict(code, "2024")
      assertNotNull(district, "District not found for code: $code")
      assertEquals(expectedLevel, district.level, "Level mismatch for code: $code")

      if (expectedLevel > 1) {
        val parent = district.code.back()
        assertNotNull(parent, "Parent not found for code: $code")

        val parentDistrict = implementationService.fetchDistrict(parent.code, "2024")
        assertNotNull(parentDistrict, "Parent district not found for code: $code")
        assertEquals(expectedLevel - 1, parentDistrict.level, "Parent level mismatch for code: $code")

        val siblings = implementationService.fetchChildren(parent.code, "2024")
        assertTrue(siblings.any { it.code.code == district.code.code }, "Parent does not contain child for code: $code")
      }
    }
  }

  @Test
  fun `reverse lookup through multiple provinces validates consistency`() {
    val provinces = listOf("11", "12", "13")

    provinces.forEach { provinceCode ->
      val province = implementationService.fetchDistrict(provinceCode, "2024")
      assertNotNull(province, "Province not found: $provinceCode")
      assertEquals(1, province.level)

      val cities = implementationService.fetchChildren(provinceCode, "2024")
      assertTrue(cities.isNotEmpty(), "No cities found for province: $provinceCode")

      cities.forEach { city ->
        assertEquals(2, city.level)
        assertTrue(city.code.code.startsWith(provinceCode))

        val parentProvince = city.code.back()
        assertNotNull(parentProvince)
        assertEquals(provinceCode, parentProvince.code)
      }
    }
  }

  // === Recursive and Traversal Tests ===

  @Test
  fun `fetchChildrenRecursive with depth control works correctly`() {
    val depth1 = implementationService.fetchChildrenRecursive("110000", 1, "2024")
    assertTrue(depth1.all { it.level == 2 })

    val depth2 = implementationService.fetchChildrenRecursive("110000", 2, "2024")
    assertTrue(depth2.any { it.level == 2 })
    assertTrue(depth2.any { it.level == 3 })

    val depth3 = implementationService.fetchChildrenRecursive("110000", 3, "2024")
    assertTrue(depth3.any { it.level == 2 })
    assertTrue(depth3.any { it.level == 3 })
    assertTrue(depth3.any { it.level == 4 })
  }

  @Test
  fun `traverseChildrenRecursive visits all nodes in correct order`() {
    val visitedNodes = mutableListOf<Pair<String, Int>>()
    val parentMap = mutableMapOf<String, String?>()

    implementationService.traverseChildrenRecursive("110000", 3, "2024") { children, depth, parent ->
      children.forEach { child ->
        visitedNodes.add(child.code.code to depth)
        parentMap[child.code.code] = parent?.code?.code
      }
      true
    }

    assertTrue(visitedNodes.any { it.second == 1 }) // Cities
    assertTrue(visitedNodes.any { it.second == 2 }) // Counties
    assertTrue(visitedNodes.any { it.second == 3 }) // Towns

    val cityNodes = visitedNodes.filter { it.second == 1 }
    cityNodes.forEach { (cityCode, _) -> assertNull(parentMap[cityCode], "City should not have parent in traversal") }

    val countyNodes = visitedNodes.filter { it.second == 2 }
    countyNodes.forEach { (countyCode, _) ->
      val parentCode = parentMap[countyCode]
      assertNotNull(parentCode, "County should have parent")
      assertTrue(cityNodes.any { it.first == parentCode })
    }
  }

  @Test
  fun `traverseChildrenRecursive early termination works correctly`() {
    val visitedNodes = mutableListOf<String>()

    implementationService.traverseChildrenRecursive("110000", 5, "2024") { children, depth, parent ->
      children.forEach { child -> visitedNodes.add(child.code.code) }
      depth < 2 // Stop after cities and counties
    }

    assertTrue(visitedNodes.any { code -> implementationService.fetchDistrict(code, "2024")?.level == 2 })
    assertTrue(visitedNodes.any { code -> implementationService.fetchDistrict(code, "2024")?.level == 3 })
    assertFalse(visitedNodes.any { code -> implementationService.fetchDistrict(code, "2024")?.level == 4 })
  }

  // === Edge Cases and Error Handling ===

  @Test
  fun `empty and invalid inputs return appropriate responses`() {
    assertTrue(implementationService.fetchChildren("", "2024").isEmpty())
    assertTrue(implementationService.fetchChildren("110000", "").isEmpty())
    assertNull(implementationService.fetchDistrict("", "2024"))
    assertNull(implementationService.fetchDistrict("110000", ""))

    assertTrue(implementationService.fetchChildren("invalid", "2024").isEmpty())
    assertTrue(implementationService.fetchChildren("999999", "2024").isEmpty())
    assertNull(implementationService.fetchDistrict("invalid", "2024"))
    assertNull(implementationService.fetchDistrict("999999", "2024"))

    assertTrue(implementationService.fetchChildren("110000", "1900").isEmpty())
    assertNull(implementationService.fetchDistrict("110000", "1900"))
  }

  @Test
  fun `leaf node detection works correctly`() {
    val village = implementationService.fetchDistrict("110101001001", "2024")
    assertNotNull(village)
    assertTrue(village.leaf, "Village should be leaf node")

    val town = implementationService.fetchDistrict("110101001000", "2024")
    assertNotNull(town)
    assertFalse(town.leaf, "Town should not be leaf node")

    val province = implementationService.fetchDistrict("110000", "2024")
    assertNotNull(province)
    assertFalse(province.leaf, "Province should not be leaf node")
  }

  @Test
  fun `year version handling and fallback behavior`() {
    assertTrue(implementationService.supportedYearVersions.contains("2024"))
    assertEquals("2024", implementationService.supportedDefaultYearVersion)

    implementationService.addSupportedYear("2023", LazyAddressCsvServiceImpl.CsvDefine("area_code_2023.csv"))
    implementationService.addSupportedYear("2022", LazyAddressCsvServiceImpl.CsvDefine("area_code_2022.csv"))

    assertEquals("2023", implementationService.lastYearVersionOrNull("2024"))
    assertEquals("2022", implementationService.lastYearVersionOrNull("2023"))
    assertNull(implementationService.lastYearVersionOrNull("2022"))
    assertNull(implementationService.lastYearVersionOrNull("invalid"))
  }

  // === Performance and Optimization Tests ===

  @Test
  fun `lazy loading behavior performs efficiently`() {
    val iterations = 10

    // Measure first load time (includes cache building)
    val firstLoadTime = measureTimeMillis { repeat(iterations) { implementationService.fetchChildren("110000", "2024") } }

    // Measure subsequent access time (should use cache)
    val cacheLoadTime = measureTimeMillis { repeat(iterations) { implementationService.fetchChildren("110000", "2024") } }

    // Cache access should be faster or at least not significantly slower
    assertTrue(cacheLoadTime <= firstLoadTime + 10, "Cache access should be efficient: cache=${cacheLoadTime}ms, initial=${firstLoadTime}ms")
  }

  @Test
  fun `backward traversal performance is optimized`() {
    val iterations = 100
    val codes = listOf("110101001001", "120102008009", "130203011012")

    val time = measureTimeMillis {
      repeat(iterations) {
        codes.forEach { code ->
          val district = implementationService.fetchDistrict(code, "2024")
          var current = district?.code
          while (current != null && !current.empty) {
            current = current.back()
          }
        }
      }
    }

    assertTrue(time < 1000, "Backward traversal should be fast: ${time}ms for $iterations iterations")
  }

  @Test
  fun `large dataset recursive operations perform within limits`() {
    val time = measureTimeMillis {
      val allDescendants = implementationService.fetchChildrenRecursive("000000000000", 5, "2024")
      assertTrue(allDescendants.isNotEmpty())
    }

    assertTrue(time < 2000, "Recursive operations should complete quickly: ${time}ms")
  }

  // === Data Integrity Tests ===

  @Test
  fun `all districts maintain proper hierarchical relationships`() {
    val allProvinces = implementationService.fetchAllProvinces("2024")

    allProvinces.forEach { province ->
      val cities = implementationService.fetchChildren(province.code.code, "2024")
      cities.forEach { city ->
        assertEquals(province.code.code, city.code.back()?.code)

        val counties = implementationService.fetchChildren(city.code.code, "2024")
        counties.forEach { county ->
          assertEquals(city.code.code, county.code.back()?.code)

          val towns = implementationService.fetchChildren(county.code.code, "2024")
          towns.forEach { town -> assertEquals(county.code.code, town.code.back()?.code) }
        }
      }
    }
  }

  @Test
  fun `code normalization consistency across all operations`() {
    val testCodes = listOf("110101", "110101000000")

    testCodes.forEach { code ->
      val district = implementationService.fetchDistrict(code, "2024")
      assertNotNull(district, "District should be found for code: $code")

      val normalizedCode = ILazyAddressService.convertToFillCode(code)
      val districtByNormalized = implementationService.fetchDistrict(normalizedCode, "2024")
      assertEquals(district.name, districtByNormalized?.name)
      assertEquals(district.level, districtByNormalized?.level)
    }
  }
}
