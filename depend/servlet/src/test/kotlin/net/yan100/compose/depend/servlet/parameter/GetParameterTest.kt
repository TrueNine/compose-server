package net.yan100.compose.depend.servlet.parameter

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.Resource
import net.yan100.compose.depend.servlet.controller.TestGetParameterController
import net.yan100.compose.testtookit.annotations.SpringServletTest
import org.apache.catalina.util.URLEncoder
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.web.bind.MissingServletRequestParameterException
import kotlin.test.*

/**
 * # 验证以何种方式 给 spring boot 传递 get 参数
 */
@SpringServletTest
class GetParameterTest {
  lateinit var mockMvc: MockMvc @Resource set
  lateinit var controller: TestGetParameterController @Resource set
  lateinit var objectMapper: ObjectMapper @Resource set

  @Test
  fun `test encode uri component`() {
    val queryParam = listOf("1,2,3", "1", "rre").joinToString(",") {
      URLEncoder.QUERY.encode(it, Charsets.UTF_8)
    }
    assertEquals("1%2C2%2C3,1,rre", queryParam)
  }


  @Test
  fun `input list str list`() {
    mockMvc.get("/test/getParameter/strList?list=1,2,3,4,5,6").andExpect {
      content { json("""["1", "2", "3", "4", "5", "6"]""") }
      status { isOk() }
    }
    mockMvc.get("/test/getParameter/strList") {
      queryParam("list", listOf("1,2,3", "1", "rre").joinToString(",") {
        URLEncoder.QUERY.encode(it, Charsets.UTF_8)
      })
    }.andExpect {
      content {
        assertFails { json("""["1,2,3","1","rre"]""") }
        json("""["1%2C2%2C3","1","rre"]""")
      }
      status { isOk() }
    }

    mockMvc.get("/test/getParameter/strList") {
      queryParam("list", "1,2,3,4,5,6")
    }.andExpect {
      content { json("""["1", "2", "3", "4", "5", "6"]""") }
      status { isOk() }
    }
  }


  @Test
  fun `non annotation`() {
    mockMvc.get("/test/getParameter/nonAnnotation") {
      queryParam("name", "1")
      queryParam("age", "2")
    }.andExpect {
      status { isOk() }
      content {
        contentType(MediaType.APPLICATION_JSON)
        json("{\"name\":\"1\",\"age\":2}")
      }
    }

    mockMvc.get("/test/getParameter/nonAnnotation") {
      queryParam("name", "1")
      queryParam("age", "2")
    }.andExpect {
      status { isOk() }
      content {
        contentType(MediaType.APPLICATION_JSON)
        json("""{"name":"1","age":2}""")
      }
    }
  }

  @Test
  fun `non annotation data class`() {
    mockMvc.get("/test/getParameter/nonAnnotationDataClass") {
      queryParam("name", "1")
      queryParam("age", "2")
    }.andExpect {
      status { isOk() }
      content {
        contentType(MediaType.APPLICATION_JSON)
        json("""{"name":"1","age":2}""")
      }
    }

    mockMvc.get("/test/getParameter/nonAnnotationDataClass") {
      queryParam("name", "1")
      queryParam("age", "2")
    }.andExpect {
      status { isOk() }
      content {
        contentType(MediaType.APPLICATION_JSON)
        json("""{"name":"1","age":2}""")
      }
    }
  }

  @Test
  fun `request param annotation`() {
    val ex = mockMvc.get("/test/getParameter/requestParam") {
      queryParam("name", "1")
      queryParam("age", "2")
    }.andExpect {
      status {
        isEqualTo(400)
      }
    }.andReturn().resolvedException
    assertNotNull(ex)
    assertIs<MissingServletRequestParameterException>(ex, "非指定异常")
  }


  @BeforeTest
  fun setup() {
    assertNotNull(mockMvc)
    assertNotNull(controller, "未扫描到 controller")
    assertNotNull(objectMapper, "未正确注册 json 解析器")
  }
}
