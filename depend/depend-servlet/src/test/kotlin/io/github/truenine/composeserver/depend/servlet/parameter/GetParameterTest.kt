package io.github.truenine.composeserver.depend.servlet.parameter

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.depend.servlet.TestApplication
import jakarta.annotation.Resource
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import org.apache.catalina.util.URLEncoder
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/** # Verify how GET parameters are passed to Spring Boot */
@SpringBootTest(classes = [TestApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(GetParameterTest.TestGetParameterController::class)
class GetParameterTest {
  lateinit var mockMvc: MockMvc
    @Resource set

  lateinit var objectMapper: ObjectMapper
    @Resource set

  @Test
  fun `URI parameter encoding should correctly encode comma-separated strings`() {
    val queryParam = listOf("1,2,3", "1", "rre").joinToString(",") { URLEncoder.QUERY.encode(it, Charsets.UTF_8) }
    assertEquals("1%2C2%2C3,1,rre", queryParam)
  }

  @Test
  fun `strList parameter passing comma-separated string returns a list of strings`() {
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
  fun `no-annotation parameter passing name and age returns a Dto object`() {
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
  fun `no-annotation parameter DataClass passing name and age returns a DataClassDto`() {
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
  fun `@RequestParam annotation missing parameter throws MissingServletRequestParameterException`() {
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
    assertIs<MissingServletRequestParameterException>(ex, "Unexpected exception type")
  }

  @BeforeTest
  fun `Test environment initialization MockMvc and ObjectMapper are injected`() {
    assertNotNull(mockMvc)
    assertNotNull(objectMapper, "JSON parser not correctly registered")
  }

  // Embedded Controller
  /** Test controller, only for this test class, path suffixed with _test to avoid ambiguity */
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

  /** Boundary case: strList parameter is empty */
  @Test
  fun `strList parameter is empty returns an empty list`() {
    mockMvc.get("/test/getParameter_test/strList?list=").andExpect {
      content { json("[]") }
      status { isOk() }
    }
  }

  /** Boundary case: strList parameter has only one element */
  @Test
  fun `strList parameter has only one element returns a single-element list`() {
    mockMvc.get("/test/getParameter_test/strList?list=onlyone").andExpect {
      content { json("[\"onlyone\"]") }
      status { isOk() }
    }
  }

  /** Boundary case: strList parameter contains special characters */
  @Test
  fun `strList parameter contains special characters returns a correctly decoded list`() {
    val special = "a,b%20c,%E4%B8%AD%E6%96%87"
    mockMvc.get("/test/getParameter_test/strList?list=$special").andExpect {
      content { json("[\"a\",\"b%20c\",\"%E4%B8%AD%E6%96%87\"]") }
      status { isOk() }
    }
  }

  /** Exception case: nonAnnotation is missing parameters */
  @Test
  fun `no-annotation parameter passing only name and not age returns a Dto object with age as null`() {
    mockMvc.get("/test/getParameter_test/nonAnnotation?name=abc").andExpect {
      status { isOk() }
      content { json("""{"name":"abc","age":null}""") }
    }
  }

  /** Exception case: nonAnnotationDataClass is missing parameters */
  @Test
  fun `no-annotation parameter DataClass passing only age and not name returns a DataClassDto with name as null`() {
    mockMvc.get("/test/getParameter_test/nonAnnotationDataClass?age=18").andExpect {
      status { isOk() }
      content { json("""{"name":null,"age":18}""") }
    }
  }

  /** Exception case: nonAnnotationDataClass parameter has an illegal type */
  @Test
  fun `no-annotation parameter DataClass passing illegal type parameter returns 400`() {
    mockMvc.get("/test/getParameter_test/nonAnnotationDataClass?name=abc&age=notanumber").andExpect { status { isBadRequest() } }
  }

  /** Exception case: requestParam is missing some parameters */
  @Test
  fun `@RequestParam annotation passing only name and not age throws MissingServletRequestParameterException`() {
    val ex = mockMvc.get("/test/getParameter_test/requestParam?name=abc").andExpect { status { isEqualTo(400) } }.andReturn().resolvedException
    assertNotNull(ex)
    assertIs<MissingServletRequestParameterException>(ex)
  }

  /** Exception case: requestParam parameter has a type error */
  @Test
  fun `@RequestParam annotation age with illegal type throws 400`() {
    mockMvc.get("/test/getParameter_test/requestParam?name=abc&age=notanumber").andExpect { status { isBadRequest() } }
  }
}
