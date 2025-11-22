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
    110000000000,Beijing Municipality,1,000000000000
    120000000000,Tianjin Municipality,1,000000000000
    130000000000,Hebei Province,1,000000000000
    110100000000,Beijing Districts,2,110000000000
    110200000000,Beijing Counties,2,110000000000
    120100000000,Tianjin Districts,2,120000000000
    130100000000,Shijiazhuang City,2,130000000000
    130200000000,Tangshan City,2,130000000000
    110101000000,Dongcheng District,3,110100000000
    110102000000,Xicheng District,3,110100000000
    110105000000,Chaoyang District,3,110100000000
    110221000000,Changping District,3,110200000000
    120101000000,Heping District,3,120100000000
    120102000000,Hedong District,3,120100000000
    130102000000,Chang'an District,3,130100000000
    130104000000,Qiaoxi District,3,130100000000
    130203000000,Guye District,3,130200000000
    110101001000,Donghuamen Subdistrict,4,110101000000
    110101002000,Jingshan Subdistrict,4,110101000000
    110101003000,Jiaodaokou Subdistrict,4,110101000000
    110102004000,Shichahai Subdistrict,4,110102000000
    110105005000,Jianguomenwai Subdistrict,4,110105000000
    110221006000,Chengbei Subdistrict,4,110221000000
    120101007000,Quanyechang Subdistrict,4,120101000000
    120102008000,Dawangzhuang Subdistrict,4,120102000000
    130102009000,Qingyuan Subdistrict,4,130102000000
    130104010000,Zhentou Subdistrict,4,130104000000
    130203011000,Linxi Subdistrict,4,130203000000
    110101001001,Donghuamen Community,5,110101001000
    110101001002,Duofuxiang Community,5,110101001000
    110101002003,Gugong Community,5,110101002000
    110101003004,Fuxuehutong Community,5,110101003000
    110102004005,Deshengmen Community,5,110102004000
    110105005006,Jiangwaijiedao Community,5,110105005000
    110221006007,Chengbei First Community,5,110221006000
    120101007008,Quanyechang Community,5,120101007000
    120102008009,Dawangzhuang First Community,5,120102008000
    130102009010,Qingyuan First Community,5,130102009000
    130104010011,Zhentou First Community,5,130104010000
    130203011012,Linxi First Community,5,130203011000
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
  fun `verifyCode valid 12-digit code returns true`() {
    assertTrue(ILazyAddressService.verifyCode("110101001001"))
  }

  @Test
  fun `verifyCode valid province code returns true`() {
    assertTrue(ILazyAddressService.verifyCode("110000000000"))
  }

  @Test
  fun `verifyCode valid city code returns true`() {
    assertTrue(ILazyAddressService.verifyCode("110100000000"))
  }

  @Test
  fun `verifyCode valid county code returns true`() {
    assertTrue(ILazyAddressService.verifyCode("110101000000"))
  }

  @Test
  fun `verifyCode valid town code returns true`() {
    assertTrue(ILazyAddressService.verifyCode("110101001000"))
  }

  @Test
  fun `verifyCode code longer than 12 digits returns false`() {
    assertFalse(ILazyAddressService.verifyCode("1101010010011"))
  }

  @Test
  fun `verifyCode code with non-digit character returns false`() {
    assertFalse(ILazyAddressService.verifyCode("11010100100A"))
  }

  @Test
  fun `convertToFillCode valid short code returns padded 12-digit code`() {
    assertEquals("110000000000", ILazyAddressService.convertToFillCode("11"))
    assertEquals("110100000000", ILazyAddressService.convertToFillCode("1101"))
    assertEquals("110101000000", ILazyAddressService.convertToFillCode("110101"))
    assertEquals("110101001000", ILazyAddressService.convertToFillCode("110101001"))
    assertEquals("110101001001", ILazyAddressService.convertToFillCode("110101001001"))
  }

  @Test
  fun `convertToFillCode invalid code returns original string`() {
    assertEquals("11010A", ILazyAddressService.convertToFillCode("11010A"))
    assertEquals("", ILazyAddressService.convertToFillCode(""))
    assertEquals("", ILazyAddressService.convertToFillCode(""))
  }

  @Test
  fun `convertToFillCode 12-digit code returns original string`() {
    assertEquals("110101001001", ILazyAddressService.convertToFillCode("110101001001"))
  }

  @Test
  fun `createCnDistrictCode with valid full code returns instance`() {
    val code = "110101001001"
    val districtCode = ILazyAddressService.createCnDistrictCode(code)
    assertNotNull(districtCode)
    assertEquals(code, districtCode.code)
    assertEquals(5, districtCode.level)
  }

  @Test
  fun `createCnDistrictCode with valid province code returns instance`() {
    val inputCode = "110000000000"
    val expectedMinimalCode = "11" // Assume CnDistrictCode.code returns minimal code
    val districtCode = ILazyAddressService.createCnDistrictCode(inputCode)
    assertNotNull(districtCode)
    assertEquals(expectedMinimalCode, districtCode.code) // Verify minimized code
    assertEquals(1, districtCode.level)
  }

  @Test
  fun `createCnDistrictCode with valid short code returns instance`() {
    val code = "1101"
    val districtCode = ILazyAddressService.createCnDistrictCode(code)
    assertNotNull(districtCode)
    assertEquals(code, districtCode.code) // Short code is already minimal
    assertEquals(2, districtCode.level) // Level is derived from code length
  }

  @Test
  fun `createCnDistrictCode with invalid code returns null`() {
    assertNull(ILazyAddressService.createCnDistrictCode("11010A"))
    assertNull(ILazyAddressService.createCnDistrictCode("1234567890123")) // Too long
    assertNull(ILazyAddressService.createCnDistrictCode(""))
    assertNull(ILazyAddressService.createCnDistrictCode(null))
  }

  // --- Default Method Tests ---

  @Test
  fun `lastYearVersionOrNull with valid year and earlier versions returns closest earlier`() {
    assertEquals("2023", service.lastYearVersionOrNull("2024"))
    assertEquals("2021", service.lastYearVersionOrNull("2023"))
  }

  @Test
  fun `lastYearVersionOrNull with earliest year returns null`() {
    assertNull(service.lastYearVersionOrNull("2021"))
  }

  @Test
  fun `lastYearVersionOrNull with unsupported year returns closest earlier`() {
    assertEquals("2021", service.lastYearVersionOrNull("2022")) // Assume supported ["2024", "2023", "2021"]
  }

  @Test
  fun `lastYearVersionOrNull with invalid year string returns null`() {
    assertNull(service.lastYearVersionOrNull("abc"))
    assertNull(service.lastYearVersionOrNull(""))
  }

  @Test
  fun `lastYearVersion returns latest supported year`() {
    assertEquals("2024", service.lastYearVersion)
  }

  @Test
  fun `lastYearVersion returns default when supported list is empty`() {
    val emptyService =
      object : ILazyAddressService {
        override val supportedYearVersions: List<String> = emptyList()
        override val supportedDefaultYearVersion: String = "2020" // Assume default year is 2020

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
  fun `supportedMaxLevel default returns 5`() {
    assertEquals(5, service.supportedMaxLevel) // Verify default implementation
  }

  // === Core Interface Implementation Tests ===

  @Test
  fun `fetchChildren returns direct children correctly`() {
    val provinces = implementationService.fetchChildren("000000000000", "2024")
    assertEquals(3, provinces.size)
    assertTrue(provinces.any { it.name == "Beijing Municipality" })
    assertTrue(provinces.any { it.name == "Tianjin Municipality" })
    assertTrue(provinces.any { it.name == "Hebei Province" })
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
    assertEquals("Dongcheng District", district.name)
    assertEquals(3, district.level)
    assertEquals("110101", district.code.code)
  }

  @Test
  fun `fetchDistrict handles code padding correctly`() {
    val districtShort = implementationService.fetchDistrict("110101", "2024")
    val districtFull = implementationService.fetchDistrict("110101000000", "2024")

    assertNotNull(districtShort)
    assertNotNull(districtFull)
    assertEquals("Dongcheng District", districtShort.name)
    assertEquals("Dongcheng District", districtFull.name)
    assertEquals(districtShort.level, districtFull.level)
  }

  // === Backward/Reverse Retrieval Mechanism Tests ===

  @Test
  fun `backward traversal from village to province works correctly`() {
    val village = implementationService.fetchDistrict("110101001001", "2024")
    assertNotNull(village)
    assertEquals(5, village.level)
    assertEquals("Donghuamen Community", village.name)

    val parentTown = village.code.back()
    assertNotNull(parentTown)
    val town = implementationService.fetchDistrict(parentTown.code, "2024")
    assertNotNull(town)
    assertEquals(4, town.level)
    assertEquals("Donghuamen Subdistrict", town.name)

    val parentCounty = town.code.back()
    assertNotNull(parentCounty)
    val county = implementationService.fetchDistrict(parentCounty.code, "2024")
    assertNotNull(county)
    assertEquals(3, county.level)
    assertEquals("Dongcheng District", county.name)

    val parentCity = county.code.back()
    assertNotNull(parentCity)
    val city = implementationService.fetchDistrict(parentCity.code, "2024")
    assertNotNull(city)
    assertEquals(2, city.level)
    assertEquals("Beijing Districts", city.name)

    val parentProvince = city.code.back()
    assertNotNull(parentProvince)
    val province = implementationService.fetchDistrict(parentProvince.code, "2024")
    assertNotNull(province)
    assertEquals(1, province.level)
    assertEquals("Beijing Municipality", province.name)

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
