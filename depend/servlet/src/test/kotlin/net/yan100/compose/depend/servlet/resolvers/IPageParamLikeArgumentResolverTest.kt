package net.yan100.compose.depend.servlet.resolvers

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.yan100.compose.domain.IPageParam
import net.yan100.compose.domain.IPageParamLike
import org.junit.jupiter.api.BeforeEach
import org.springframework.core.MethodParameter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.NativeWebRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IPageParamLikeArgumentResolverTest {

  private val resolver = IPageParamLikeArgumentResolver()
  private lateinit var mockMvc: MockMvc

  @BeforeEach
  fun setup() {
    // 使用 standaloneSetup 明确指定 Controller 和 ArgumentResolver
    mockMvc = MockMvcBuilders.standaloneSetup(TestController())
      .setCustomArgumentResolvers(IPageParamLikeArgumentResolver())
      .build()
  }

  @RestController
  internal class TestController {
    @GetMapping("/test-page")
    fun testPageParam(pageParam: IPageParamLike): IPageParamLike {
      return pageParam
    }
  }

  // --- supportsParameter Tests ---

  @Test
  fun `supportsParameter 当参数类型为 IPageParamLike 时返回 true`() {
    val mockParameter = mockk<MethodParameter>()
    every { mockParameter.parameterType } returns IPageParamLike::class.java

    assertTrue(resolver.supportsParameter(mockParameter))
  }

  @Test
  fun `supportsParameter 当参数类型不为 IPageParamLike 时返回 false`() {
    val mockParameter = mockk<MethodParameter>()
    every { mockParameter.parameterType } returns String::class.java // Different type

    assertFalse(resolver.supportsParameter(mockParameter))
  }

  // --- resolveArgument Tests ---

  @Test
  fun `resolveArgument 正常解析所有参数`() {
    val mockParameter = mockk<MethodParameter>()
    val mockWebRequest = mockk<NativeWebRequest>()

    every { mockWebRequest.getParameter("o") } returns "10"
    every { mockWebRequest.getParameter("s") } returns "20"
    every { mockWebRequest.getParameter("u") } returns "true"

    val expected = IPageParam[10, 20, true]
    val actual = resolver.resolveArgument(mockParameter, null, mockWebRequest, null)

    assertEquals(expected, actual)
    verify { mockWebRequest.getParameter("o") }
    verify { mockWebRequest.getParameter("s") }
    verify { mockWebRequest.getParameter("u") }
  }

  @Test
  fun `resolveArgument 部分参数缺失时使用默认值`() {
    val mockParameter = mockk<MethodParameter>()
    val mockWebRequest = mockk<NativeWebRequest>()

    every { mockWebRequest.getParameter("o") } returns "5"
    every { mockWebRequest.getParameter("s") } returns null // Missing 's'
    every { mockWebRequest.getParameter("u") } returns null // Missing 'u'

    // Assuming IPageParam companion object handles nulls with defaults
    val expected = IPageParam[5, null, null]
    val actual = resolver.resolveArgument(mockParameter, null, mockWebRequest, null)

    assertEquals(expected, actual)
    verify { mockWebRequest.getParameter("o") }
    verify { mockWebRequest.getParameter("s") }
    verify { mockWebRequest.getParameter("u") }
  }

  @Test
  fun `resolveArgument 所有参数缺失时使用默认值`() {
    val mockParameter = mockk<MethodParameter>()
    val mockWebRequest = mockk<NativeWebRequest>()

    every { mockWebRequest.getParameter("o") } returns null
    every { mockWebRequest.getParameter("s") } returns null
    every { mockWebRequest.getParameter("u") } returns null

    val expected = IPageParam[null, null, null] // Expecting defaults
    val actual = resolver.resolveArgument(mockParameter, null, mockWebRequest, null)

    assertEquals(expected, actual)
    verify { mockWebRequest.getParameter("o") }
    verify { mockWebRequest.getParameter("s") }
    verify { mockWebRequest.getParameter("u") }
  }

  @Test
  fun `resolveArgument 参数格式无效时使用默认值`() {
    val mockParameter = mockk<MethodParameter>()
    val mockWebRequest = mockk<NativeWebRequest>()

    every { mockWebRequest.getParameter("o") } returns "abc" // Invalid integer
    every { mockWebRequest.getParameter("s") } returns "xyz" // Invalid integer
    every { mockWebRequest.getParameter("u") } returns "maybe" // Invalid boolean

    val expected = IPageParam[null, null, null] // Expecting defaults due to parsing errors
    val actual = resolver.resolveArgument(mockParameter, null, mockWebRequest, null)

    assertEquals(expected, actual)
    verify { mockWebRequest.getParameter("o") }
    verify { mockWebRequest.getParameter("s") }
    verify { mockWebRequest.getParameter("u") }
  }

  // --- MockMvc Integration Tests ---

  @Test
  fun `resolveArgument 使用 MockMvc 正常解析所有参数`() {
    mockMvc.perform(get("/test-page?o=15&s=30&u=false"))
      .andExpect(status().isOk)
      .andExpect(jsonPath("$.o").value(15))
      .andExpect(jsonPath("$.s").value(30))
      .andExpect(jsonPath("$.u").value(false))
  }

  @Test
  fun `resolveArgument 使用 MockMvc 部分参数缺失时使用默认值`() {
    mockMvc.perform(get("/test-page?o=5"))
      .andExpect(status().isOk)
      .andExpect(jsonPath("$.o").value(5))
      .andExpect(jsonPath("$.s").value(42))
      .andExpect(jsonPath("$.u").value(null))
  }

  @Test
  fun `resolveArgument 使用 MockMvc 所有参数缺失时使用默认值`() {
    mockMvc.perform(get("/test-page"))
      .andExpect(status().isOk)
      .andExpect(jsonPath("$.o").value(0))
      .andExpect(jsonPath("$.s").value(42))
      .andExpect(jsonPath("$.u").value(null))
  }

  @Test
  fun `resolveArgument 使用 MockMvc 参数格式无效时使用默认值`() {
    mockMvc.perform(get("/test-page?o=abc&s=xyz&u=maybe"))
      .andExpect(status().isOk)
      .andExpect(jsonPath("$.o").value(0))
      .andExpect(jsonPath("$.s").value(42))
      .andExpect(jsonPath("$.u").value(null))
  }
} 
