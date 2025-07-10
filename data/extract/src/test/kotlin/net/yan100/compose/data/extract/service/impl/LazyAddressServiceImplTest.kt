package net.yan100.compose.data.extract.service.impl

import io.mockk.every
import io.mockk.mockk
import kotlin.test.Ignore
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import net.yan100.compose.data.extract.api.ICnNbsAddressApi
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
  fun `traverseChildrenRecursive 正常递归遍历所有子节点`() {
    // 模拟数据：省-市-区
    every { chstApi.homePage().body } returns "<html><body><tr class='provincetr'><td><a href='./110000.html'>北京市</a></td></tr></body></html>"
    every { chstApi.getCityPage(any()) } returns
      mockk(relaxed = true) {
        every { body } returns
          "<html><body><tr class='citytr'><td><a href='110100.html'>北京市市辖区</a></td><td><a href='110100.html'>北京市市辖区</a></td></tr></body></html>"
      }
    every { chstApi.getCountyPage(any(), any()) } returns
      mockk(relaxed = true) { every { body } returns "<html><body><tr class='countytr'><td>110101</td><td>东城区</td></tr></body></html>" }
    // 遍历
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
  fun `traverseChildrenRecursive 回调返回false时中断分支`() {
    every { chstApi.homePage().body } returns "<html><body><tr class='provincetr'><td><a href='./110000.html'>北京市</a></td></tr></body></html>"
    every { chstApi.getCityPage(any()) } returns
      mockk(relaxed = true) {
        every { body } returns
          "<html><body><tr class='citytr'><td><a href='110100.html'>北京市市辖区</a></td><td><a href='110100.html'>北京市市辖区</a></td></tr></body></html>"
      }
    every { chstApi.getCountyPage(any(), any()) } returns
      mockk(relaxed = true) { every { body } returns "<html><body><tr class='countytr'><td>110101</td><td>东城区</td></tr></body></html>" }
    val visited = mutableListOf<String>()
    service.traverseChildrenRecursive("000000000000", 3, "2023") { children, depth, parent ->
      children.forEach { district -> visited += district.code.code }
      // 只遍历到省级
      children.all { it.level < 1 }
    }
    assertTrue(visited.contains("110000"))
    assertFalse(visited.contains("110100"))
    assertFalse(visited.contains("110101"))
  }

  @Test
  fun `traverseChildrenRecursive 只遍历一层`() {
    every { chstApi.homePage().body } returns "<html><body><tr class='provincetr'><td><a href='./110000.html'>北京市</a></td></tr></body></html>"
    every { chstApi.getCityPage(any()) } returns
      mockk(relaxed = true) {
        every { body } returns
          "<html><body><tr class='citytr'><td><a href='110100.html'>北京市市辖区</a></td><td><a href='110100.html'>北京市市辖区</a></td></tr></body></html>"
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
  fun `traverseChildrenRecursive parentDistrict 参数正确`() {
    every { chstApi.homePage().body } returns "<html><body><tr class='provincetr'><td><a href='./110000.html'>北京市</a></td></tr></body></html>"
    every { chstApi.getCityPage(any()) } returns
      mockk(relaxed = true) {
        every { body } returns
          "<html><body><tr class='citytr'><td><a href='110100.html'>北京市市辖区</a></td><td><a href='110100.html'>北京市市辖区</a></td></tr></body></html>"
      }
    every { chstApi.getCountyPage(any(), any()) } returns
      mockk(relaxed = true) { every { body } returns "<html><body><tr class='countytr'><td>110101</td><td>东城区</td></tr></body></html>" }
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
  fun `traverseChildrenRecursive 空数据和无效parentCode`() {
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
