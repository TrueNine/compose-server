package net.yan100.compose.depend.servlet.parameter

import jakarta.annotation.Resource
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.mock.web.MockPart
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.multipart
import java.nio.charset.StandardCharsets
import kotlin.test.Test

@AutoConfigureMockMvc
@SpringBootTest
class UploadFileParamAssertTest {
  lateinit var mvc: MockMvc @Resource set
  val json = """{"a":"str","b": 1023}"""
  val jsonFile = MockMultipartFile(
    "json",
    "json",
    "application/json",
    json.toByteArray(StandardCharsets.UTF_8)
  )
  val file = MockMultipartFile(
    "file",
    "file",
    "text/plain",
    "Hello, World!".toByteArray()
  )

  @Test
  fun `测试 混合 dto 上传`() {
    mvc.multipart(HttpMethod.POST, "/testUploadController/uploadBlend") {
      param("json.a", "str")
      param("json.b", "1")
      part(MockPart("file", "", "Hello, World!".toByteArray(), MediaType.TEXT_PLAIN))
    }.andExpect {
      status { isOk() }
    }
  }

  @Test
  fun `测试 上传 json 表单以及 文件`() {
    mvc.multipart(HttpMethod.POST, "/testUploadController/uploadFileAndOtherField") {
      contentType = MediaType.MULTIPART_FORM_DATA
      file(jsonFile)
      file(file)
    }.andExpect {
      status { isOk() }
    }
  }
}
