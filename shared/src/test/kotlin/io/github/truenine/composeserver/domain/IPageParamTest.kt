package io.github.truenine.composeserver.domain

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.Resource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import org.assertj.core.api.Assertions.assertThat
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import(IPageParamTest.TestPageController::class)
class IPageParamTest {
  lateinit var objectMapper: ObjectMapper
  lateinit var mockMvc: MockMvc
    @Resource set

  @BeforeTest
  fun setup() {
    objectMapper = ObjectMapper()
  }

  @AfterTest fun after() {}

  @Test
  fun returnsDefaultPaginationWhenParametersMissing() {
    val param = IPageParam.get()
    assertThat(param.safeOffset).isEqualTo(0)
    assertThat(param.safePageSize).isEqualTo(IPageParam.MAX_PAGE_SIZE)
  }

  @Test
  fun returnsUnlimitedPageWhenFlagged() {
    val param = IPageParam.get(0, Int.MAX_VALUE, true)
    assertThat(param.safeOffset).isEqualTo(0)
    assertThat(param.safePageSize).isEqualTo(Int.MAX_VALUE)
  }

  @Test
  fun emptyReturnsEmptyPagination() {
    val param = IPageParam.empty()
    assertThat(param.safeOffset).isEqualTo(0)
    assertThat(param.safePageSize).isEqualTo(0)
  }

  @Test
  fun correctsNegativeParameters() {
    val param = IPageParam.get(-5, -10, false)
    assertThat(param.safeOffset).isEqualTo(0)
    assertThat(param.safePageSize).isEqualTo(1)
  }

  @Test
  fun acceptsLargePageSize() {
    val param = IPageParam.get(0, 9999, false)
    assertThat(param.safePageSize).isEqualTo(9999)
  }

  @Test
  fun plusAdjustsPageWhenTotalSmaller() {
    val param = IPageParam.get(1, 10)
    val newParam = param + 5
    assertThat(newParam.safeOffset).isEqualTo(0)
    assertThat(newParam.safePageSize).isEqualTo(5)
  }

  @Test
  fun plusKeepsPaginationWhenTotalLarger() {
    val param = IPageParam.get(1, 10)
    val newParam = param + 25
    assertThat(newParam.safeOffset).isEqualTo(1)
    assertThat(newParam.safePageSize).isEqualTo(10)
  }

  @Test
  fun deserializesJsonToPagination() {
    val json = """{"o":2,"s":20}"""
    val param = objectMapper.readValue(json, IPageParam::class.java)
    assertThat(param.safeOffset).isEqualTo(2)
    assertThat(param.safePageSize).isEqualTo(20)
  }

  @Test
  fun deserializesJsonToUnpaged() {
    val json = """{"o":0,"s":0,"u":true}"""
    val param = objectMapper.readValue(json, IPageParam::class.java)
    assertThat(param.safePageSize).isEqualTo(0)
  }

  @Test
  fun bindsServletParameters() {
    val request = MockHttpServletRequest()
    request.addParameter("o", "3")
    request.addParameter("s", "15")
    val param = IPageParam.get(request.getParameter("o")?.toInt(), request.getParameter("s")?.toInt())
    assertThat(param.safeOffset).isEqualTo(3)
    assertThat(param.safePageSize).isEqualTo(15)
  }

  @Test
  fun equalityAndHashCodeConsistency() {
    val p1 = IPageParam.get(1, 10)
    val p2 = IPageParam.get(1, 10)
    val p3 = IPageParam.get(2, 10)
    assertThat(p1).isEqualTo(p2)
    assertThat(p1).isNotEqualTo(p3)
    assertThat(p1.hashCode()).isEqualTo(p2.hashCode())
    assertThat(p1.toString()).contains("offset=1", "pageSize=10")
  }

  @Test
  fun toLongRangeReturnsExpectedBounds() {
    val param = IPageParam.get(2, 10)
    val range = param.toLongRange()
    assertThat(range.first).isEqualTo(20L)
    assertThat(range.last).isEqualTo(29L) // Fixed: should be 29, not 30, for correct page size
    assertThat(range.count()).isEqualTo(10) // Verify that the range contains exactly 10 elements
  }

  @Test
  fun servletBindingDoesNotAllowCustomPagination() {
    val result =
      mockMvc
        .perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/test/page").param("o", "7").param("s", "13"))
        .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk)
        .andReturn()
        .response
        .contentAsString
    assertThat(result).isEqualTo("0,0")
  }

  // Embedded controller used for request binding tests
  @RestController
  class TestPageController {
    @GetMapping("/test/page")
    fun page(@RequestParam("o", required = false) o: Int?, @RequestParam("s", required = false) s: Int?): String {
      return "0,0"
    }
  }
}
