package io.github.truenine.composeserver.data.extract.service.impl

import io.github.truenine.composeserver.data.extract.api.ICnNbsAddressApi
import io.mockk.every
import io.mockk.mockk
import kotlin.test.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@Ignore
class LazyAddressServiceImplTest {
  private lateinit var chstApi: ICnNbsAddressApi
  private lateinit var service: LazyAddressServiceImpl

  @BeforeEach
  fun setup() {
    chstApi = mockk(relaxed = true)
    service = LazyAddressServiceImpl(chstApi)
  }

  @Test
  fun `traverseChildrenRecursive traverses all child nodes`() {
    // Mock data: province -> city -> county
    every { chstApi.homePage().body } returns "<html><body><tr class='provincetr'><td><a href='./110000.html'>Beijing Municipality</a></td></tr></body></html>"
    every { chstApi.getCityPage(any()) } returns
      mockk(relaxed = true) {
        every { body } returns
          "<html><body><tr class='citytr'><td><a href='110100.html'>Beijing Districts</a></td><td><a href='110100.html'>Beijing Districts</a></td></tr></body></html>"
      }
    every { chstApi.getCountyPage(any(), any()) } returns
      mockk(relaxed = true) { every { body } returns "<html><body><tr class='countytr'><td>110101</td><td>Dongcheng District</td></tr></body></html>" }
    // Traverse
    val visited = mutableListOf<Pair<String, Int>>()
    service.traverseChildrenRecursive("000000000000", 3, "2023") { children, depth, parent ->
      children.forEach { district -> visited += district.code.code to depth }
      true
    }
    assertTrue(visited.any { it.first == "110000" && it.second == 1 })
    assertTrue(visited.any { it.first == "110100" && it.second == 2 })
    assertTrue(visited.any { it.first == "110101" && it.second == 3 })
  }

  @Test
  fun `traverseChildrenRecursive stops branch when callback returns false`() {
    every { chstApi.homePage().body } returns "<html><body><tr class='provincetr'><td><a href='./110000.html'>Beijing Municipality</a></td></tr></body></html>"
    every { chstApi.getCityPage(any()) } returns
      mockk(relaxed = true) {
        every { body } returns
          "<html><body><tr class='citytr'><td><a href='110100.html'>Beijing Districts</a></td><td><a href='110100.html'>Beijing Districts</a></td></tr></body></html>"
      }
    every { chstApi.getCountyPage(any(), any()) } returns
      mockk(relaxed = true) { every { body } returns "<html><body><tr class='countytr'><td>110101</td><td>Dongcheng District</td></tr></body></html>" }
    val visited = mutableListOf<String>()
    service.traverseChildrenRecursive("000000000000", 3, "2023") { children, depth, parent ->
      children.forEach { district -> visited += district.code.code }
      // Traverse only to province level
      children.all { it.level < 1 }
    }
    assertTrue(visited.contains("110000"))
    assertFalse(visited.contains("110100"))
    assertFalse(visited.contains("110101"))
  }

  @Test
  fun `traverseChildrenRecursive single level`() {
    every { chstApi.homePage().body } returns "<html><body><tr class='provincetr'><td><a href='./110000.html'>Beijing Municipality</a></td></tr></body></html>"
    every { chstApi.getCityPage(any()) } returns
      mockk(relaxed = true) {
        every { body } returns
          "<html><body><tr class='citytr'><td><a href='110100.html'>Beijing Districts</a></td><td><a href='110100.html'>Beijing Districts</a></td></tr></body></html>"
      }
    val visited = mutableListOf<String>()
    service.traverseChildrenRecursive("000000000000", 1, "2023") { children, depth, parent ->
      children.forEach { district -> visited += district.code.code }
      true
    }
    assertTrue(visited.contains("110000"))
    assertFalse(visited.contains("110100"))
  }

  @Test
  fun `traverseChildrenRecursive parentDistrict parameter is correct`() {
    every { chstApi.homePage().body } returns "<html><body><tr class='provincetr'><td><a href='./110000.html'>Beijing Municipality</a></td></tr></body></html>"
    every { chstApi.getCityPage(any()) } returns
      mockk(relaxed = true) {
        every { body } returns
          "<html><body><tr class='citytr'><td><a href='110100.html'>Beijing Districts</a></td><td><a href='110100.html'>Beijing Districts</a></td></tr></body></html>"
      }
    every { chstApi.getCountyPage(any(), any()) } returns
      mockk(relaxed = true) { every { body } returns "<html><body><tr class='countytr'><td>110101</td><td>Dongcheng District</td></tr></body></html>" }
    val parentMap = mutableMapOf<String, String?>()
    service.traverseChildrenRecursive("000000000000", 3, "2023") { children, depth, parent ->
      children.forEach { district -> parentMap[district.code.code] = parent?.code?.code }
      true
    }
    assertEquals("000000000000", parentMap["110000"])
    assertEquals("110000", parentMap["110100"])
    assertEquals("110100", parentMap["110101"])
  }

  @Test
  fun `traverseChildrenRecursive empty data and invalid parentCode`() {
    every { chstApi.homePage().body } returns "<html><body></body></html>"
    val visited = mutableListOf<String>()
    service.traverseChildrenRecursive("999999", 3, "2023") { children, depth, parent ->
      children.forEach { district -> visited += district.code.code }
      true
    }
    assertTrue(visited.isEmpty())

    val visited2 = mutableListOf<String>()
    service.traverseChildrenRecursive("invalid", 3, "2023") { children, depth, parent ->
      children.forEach { district -> visited2 += district.code.code }
      true
    }
    assertTrue(visited2.isEmpty())
  }
}
