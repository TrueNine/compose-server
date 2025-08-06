package io.github.truenine.composeserver.depend.servlet.resolvers

import io.github.truenine.composeserver.Pq
import io.github.truenine.composeserver.domain.IPageParam
import io.github.truenine.composeserver.domain.IPageParamLike
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.springframework.core.MethodParameter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.NativeWebRequest

class IPageParamLikeArgumentResolverTest {

  private val resolver = IPageParamLikeArgumentResolver()
  private lateinit var mockMvc: MockMvc

  @BeforeEach
  fun setup() {
    // 使用 standaloneSetup 明确指定 Controller 和 ArgumentResolver
    mockMvc = MockMvcBuilders.standaloneSetup(TestController()).setCustomArgumentResolvers(IPageParamLikeArgumentResolver()).build()
  }

  data class IPageParamLikeImpl(override val o: Int?, override val s: Int?) : IPageParamLike

  data class IPageParamLikeDefaultValueImpl(val e: String) : IPageParamLike

  @RestController
  internal class TestController {
    @GetMapping("/test_page")
    fun testPageParam(pageParam: IPageParamLike): IPageParamLike {
      return pageParam
    }

    @GetMapping("/to_default")
    fun toDefault(defaultValue: IPageParamLikeDefaultValueImpl): IPageParamLikeDefaultValueImpl {
      return defaultValue
    }

    @GetMapping("/to_pq")
    fun toPq(pq: Pq): Pq {
      return pq
    }

    @GetMapping("/to_impl")
    fun toImpl(pqImpl: IPageParamLikeImpl): IPageParamLikeImpl {
      return pqImpl
    }
  }

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
    every { mockWebRequest.getParameter("u") } returns null

    val expected = IPageParam[10, 20]
    val actual = resolver.resolveArgument(mockParameter, null, mockWebRequest, null)

    assertEquals(expected, actual)
    verify { mockWebRequest.getParameter("o") }
    verify { mockWebRequest.getParameter("s") }
  }

  @Test
  fun `resolveArgument 部分参数缺失时使用默认值`() {
    val mockParameter = mockk<MethodParameter>()
    val mockWebRequest = mockk<NativeWebRequest>()

    every { mockWebRequest.getParameter("o") } returns "5"
    every { mockWebRequest.getParameter("s") } returns null
    every { mockWebRequest.getParameter("u") } returns null

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

  @Test
  fun `resolveArgument 使用 MockMvc 正常解析所有参数`() {
    mockMvc
      .perform(get("/test_page?o=15&s=30&u=false"))
      .andExpect(status().isOk)
      .andExpect(jsonPath("$.o").value(15))
      .andExpect(jsonPath("$.s").value(30))
      .andExpect(jsonPath("$.u").value(false))
  }

  @Test
  fun `resolveArgument 使用 MockMvc 部分参数缺失时使用默认值`() {
    mockMvc
      .perform(get("/test_page?o=5"))
      .andExpect(status().isOk)
      .andExpect(jsonPath("$.o").value(5))
      .andExpect(jsonPath("$.s").value(42))
      .andExpect(jsonPath("$.u").value(null))
  }

  @Test
  fun `resolveArgument 使用 MockMvc 所有参数缺失时使用默认值`() {
    mockMvc
      .perform(get("/test_page"))
      .andExpect(status().isOk)
      .andExpect(jsonPath("$.o").value(0))
      .andExpect(jsonPath("$.s").value(42))
      .andExpect(jsonPath("$.u").value(null))
  }

  @Test
  fun `resolveArgument 使用 MockMvc 参数格式无效时使用默认值`() {
    mockMvc.get("/test_page?o=abc&s=xyz&u=maybe").andExpect {
      status { isOk() }
      content {
        jsonPath("$.o").value(0)
        jsonPath("$.s").value(42)
        jsonPath("$.u").value(null)
      }
    }
  }

  @Test
  fun `toImpl 端点正确处理IPageParamLikeImpl类型参数`() {
    mockMvc.get("/to_impl?o=10&s=20").andExpect {
      status { isOk() }
      content {
        jsonPath("$.o").value(10)
        jsonPath("$.s").value(20)
      }
    }
  }

  @Test
  fun `toPq 端点正确处理IPageParamLikeImpl类型参数`() {
    mockMvc.get("/to_pq?o=10&s=20").andExpect {
      status { isOk() }
      content {
        jsonPath("$.o").value(10)
        jsonPath("$.s").value(20)
      }
    }
  }

  @Test
  fun `toDefault 端点正确处理IPageParamLikeImpl类型参数`() {
    mockMvc.get("/to_default?o=10&s=20&e=exception").andExpect {
      status { isOk() }
      content {
        jsonPath("$.o").value(10)
        jsonPath("$.s").value(20)
        jsonPath("$.e").value("exception")
      }
    }
  }
}
