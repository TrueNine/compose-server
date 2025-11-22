package io.github.truenine.composeserver.depend.servlet.parameter

import io.github.truenine.composeserver.depend.servlet.TestApplication
import java.nio.charset.StandardCharsets
import kotlin.test.BeforeTest
import kotlin.test.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.mock.web.MockPart
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.multipart
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.multipart.MultipartFile

@SpringBootTest(classes = [TestApplication::class])
@Import(UploadFileParamAssertTest.TestUploadController::class)
class UploadFileParamAssertTest {
  @Autowired
  lateinit var webApplicationContext: WebApplicationContext

  lateinit var mvc: MockMvc

  @BeforeTest
  fun setup() {
    mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
  }

  val json = """{"a":"str","b": 1023}"""
  val jsonFile = MockMultipartFile("json", "json", "application/json", json.toByteArray(StandardCharsets.UTF_8))
  val file = MockMultipartFile("file", "file", "text/plain", "Hello, World!".toByteArray())

  @Test
  fun `Test mixed DTO upload`() {
    mvc
      .multipart(HttpMethod.POST, "/testUploadController/uploadBlend") {
        param("json.a", "str")
        param("json.b", "1")
        part(MockPart("file", "", "Hello, World!".toByteArray(), MediaType.TEXT_PLAIN))
      }
      .andExpect { status { isOk() } }
  }

  @Test
  fun `Test uploading JSON form and file`() {
    mvc
      .multipart(HttpMethod.POST, "/testUploadController/uploadFileAndOtherField") {
        contentType = MediaType.MULTIPART_FORM_DATA
        file(jsonFile)
        file(file)
      }
      .andExpect { status { isOk() } }
  }

  // Embedded Controller
  @RequestMapping("testUploadController")
  @RestController
  class TestUploadController {
    open class JsonDto(val a: String, val b: Int)

    open class BlendDto(open val json: JsonDto, open val file: MultipartFile)

    open class FileList(val a: String, val jsonDto: JsonDto)

    @PostMapping("uploadFileList") fun uploadFileList(file: FileList) {}

    @PostMapping("uploadBlend")
    fun uploadBlend(blendDto: BlendDto) {
      kotlin.test.assertNotNull(blendDto.json)
      kotlin.test.assertEquals("str", blendDto.json.a)
      kotlin.test.assertEquals(1, blendDto.json.b)
      kotlin.test.assertNotNull(blendDto.file)
      kotlin.test.assertFalse(blendDto.file.isEmpty)
    }

    @PostMapping("uploadFileAndOtherField")
    fun uploadFileAndOtherField(@RequestPart json: JsonDto, @RequestPart file: MultipartFile) {
      kotlin.test.assertNotNull(json.a)
      kotlin.test.assertNotNull(json.b)
      kotlin.test.assertNotNull(file)
      kotlin.test.assertFalse { file.isEmpty }
    }
  }
}
