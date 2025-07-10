package net.yan100.compose.depend.servlet.parameter

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.Resource
import kotlin.test.*
import net.yan100.compose.testtoolkit.annotations.SpringServletTest
import org.apache.catalina.util.URLEncoder
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/** # 验证以何种方式 给 spring boot 传递 get 参数 */
@SpringServletTest
@Import(GetParameterTest.TestGetParameterController::class)
class GetParameterTest {
  lateinit var mockMvc: MockMvc
    @Resource set

  lateinit var objectMapper: ObjectMapper
    @Resource set

  @Test
  fun `URI参数编码 应正确编码逗号分隔字符串`() {
    val queryParam = listOf("1,2,3", "1", "rre").joinToString(",") { URLEncoder.QUERY.encode(it, Charsets.UTF_8) }
    assertEquals("1%2C2%2C3,1,rre", queryParam)
  }

  @Test
  fun `strList参数 传递逗号分隔字符串 返回字符串列表`() {
    mockMvc.get("/test/getParameter_test/strList?list=1,2,3,4,5,6").andExpect {
      content { json("""["1", "2", "3", "4", "5", "6"]""") }
      status { isOk() }
    }
    mockMvc
      .get("/test/getParameter_test/strList") {
        queryParam("list", listOf("1,2,3", "1", "rre").joinToString(",") { URLEncoder.QUERY.encode(it, Charsets.UTF_8) })
      }
      .andExpect {
        content {
          assertFails { json("""["1,2,3","1","rre"]""") }
          json("""["1%2C2%2C3","1","rre"]""")
        }
        status { isOk() }
      }

    mockMvc
      .get("/test/getParameter_test/strList") { queryParam("list", "1,2,3,4,5,6") }
      .andExpect {
        content { json("""["1", "2", "3", "4", "5", "6"]""") }
        status { isOk() }
      }
  }

  @Test
  fun `无注解参数 传递name和age 返回Dto对象`() {
    mockMvc
      .get("/test/getParameter_test/nonAnnotation") {
        queryParam("name", "1")
        queryParam("age", "2")
      }
      .andExpect {
        status { isOk() }
        content {
          contentType(MediaType.APPLICATION_JSON)
          json("{\"name\":\"1\",\"age\":2}")
        }
      }

    mockMvc
      .get("/test/getParameter_test/nonAnnotation") {
        queryParam("name", "1")
        queryParam("age", "2")
      }
      .andExpect {
        status { isOk() }
        content {
          contentType(MediaType.APPLICATION_JSON)
          json("""{"name":"1","age":2}""")
        }
      }
  }

  @Test
  fun `无注解参数 DataClass 传递name和age 返回DataClassDto`() {
    mockMvc
      .get("/test/getParameter_test/nonAnnotationDataClass") {
        queryParam("name", "1")
        queryParam("age", "2")
      }
      .andExpect {
        status { isOk() }
        content {
          contentType(MediaType.APPLICATION_JSON)
          json("""{"name":"1","age":2}""")
        }
      }

    mockMvc
      .get("/test/getParameter_test/nonAnnotationDataClass") {
        queryParam("name", "1")
        queryParam("age", "2")
      }
      .andExpect {
        status { isOk() }
        content {
          contentType(MediaType.APPLICATION_JSON)
          json("""{"name":"1","age":2}""")
        }
      }
  }

  @Test
  fun `@RequestParam注解 缺少参数时 抛出MissingServletRequestParameterException`() {
    val ex =
      mockMvc
        .get("/test/getParameter_test/requestParam") {
          queryParam("name", "1")
          queryParam("age", "2")
        }
        .andExpect { status { isEqualTo(400) } }
        .andReturn()
        .resolvedException
    assertNotNull(ex)
    assertIs<MissingServletRequestParameterException>(ex, "非指定异常")
  }

  @BeforeTest
  fun `测试环境初始化 MockMvc和ObjectMapper已注入`() {
    assertNotNull(mockMvc)
    assertNotNull(objectMapper, "未正确注册 json 解析器")
  }

  // 内嵌 Controller
  /** 测试用 Controller，仅用于本测试类，路径加 _test 后缀避免歧义 */
  @RestController
  @RequestMapping("test/getParameter_test")
  class TestGetParameterController {
    open class Dto {
      var name: String? = null
      var age: Int? = null
    }

    data class DataClassDto(var name: String? = null, var age: Int? = null)

    @GetMapping("nonAnnotation") fun nonAnnotation(dto: Dto): Dto = dto

    @GetMapping("requestParam") fun requestParam(@RequestParam dto: Dto): Dto = dto

    @GetMapping("nonAnnotationDataClass") fun nonAnnotationDataClass(dto: DataClassDto) = dto

    @GetMapping("strList") fun inputStringList(@RequestParam list: List<String>): List<String> = list
  }

  /** 边界用例：strList参数为空 */
  @Test
  fun `strList参数 为空 返回空列表`() {
    mockMvc.get("/test/getParameter_test/strList?list=").andExpect {
      content { json("[]") }
      status { isOk() }
    }
  }

  /** 边界用例：strList参数只传一个元素 */
  @Test
  fun `strList参数 只传一个元素 返回单元素列表`() {
    mockMvc.get("/test/getParameter_test/strList?list=onlyone").andExpect {
      content { json("[\"onlyone\"]") }
      status { isOk() }
    }
  }

  /** 边界用例：strList参数包含特殊字符 */
  @Test
  fun `strList参数 包含特殊字符 返回正确解码列表`() {
    val special = "a,b%20c,%E4%B8%AD%E6%96%87"
    mockMvc.get("/test/getParameter_test/strList?list=$special").andExpect {
      content { json("[\"a\",\"b%20c\",\"%E4%B8%AD%E6%96%87\"]") }
      status { isOk() }
    }
  }

  /** 异常用例：nonAnnotation缺少参数 */
  @Test
  fun `无注解参数 只传name 不传age 返回Dto对象age为null`() {
    mockMvc.get("/test/getParameter_test/nonAnnotation?name=abc").andExpect {
      status { isOk() }
      content { json("""{"name":"abc","age":null}""") }
    }
  }

  /** 异常用例：nonAnnotationDataClass缺少参数 */
  @Test
  fun `无注解参数 DataClass 只传age 不传name 返回DataClassDto name为null`() {
    mockMvc.get("/test/getParameter_test/nonAnnotationDataClass?age=18").andExpect {
      status { isOk() }
      content { json("""{"name":null,"age":18}""") }
    }
  }

  /** 异常用例：nonAnnotationDataClass参数为非法类型 */
  @Test
  fun `无注解参数 DataClass 传递非法类型参数 返回400`() {
    mockMvc.get("/test/getParameter_test/nonAnnotationDataClass?name=abc&age=notanumber").andExpect { status { isBadRequest() } }
  }

  /** 异常用例：requestParam缺少部分参数 */
  @Test
  fun `@RequestParam注解 只传name 不传age 抛出MissingServletRequestParameterException`() {
    val ex = mockMvc.get("/test/getParameter_test/requestParam?name=abc").andExpect { status { isEqualTo(400) } }.andReturn().resolvedException
    assertNotNull(ex)
    assertIs<MissingServletRequestParameterException>(ex)
  }

  /** 异常用例：requestParam参数类型错误 */
  @Test
  fun `@RequestParam注解 age为非法类型 抛出400`() {
    mockMvc.get("/test/getParameter_test/requestParam?name=abc&age=notanumber").andExpect { status { isBadRequest() } }
  }
}
