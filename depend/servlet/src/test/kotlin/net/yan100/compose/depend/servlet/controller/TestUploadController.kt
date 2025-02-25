package net.yan100.compose.depend.servlet.controller

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RequestMapping("testUploadController")
@RestController
class TestUploadController {
  open class JsonDto(val a: String, val b: Int)

  open class BlendDto(open val json: JsonDto, open val file: MultipartFile)

  open class FileList(val a: String, val jsonDto: JsonDto)

  @PostMapping("uploadFileList") fun uploadFileList(file: FileList) {}

  @PostMapping("uploadBlend")
  fun uploadBlend(blendDto: BlendDto) {
    assertNotNull(blendDto.json)
    assertEquals("str", blendDto.json.a)
    assertEquals(1, blendDto.json.b)
    assertNotNull(blendDto.file)
    assertFalse(blendDto.file.isEmpty)
  }

  @PostMapping("uploadFileAndOtherField")
  fun uploadFileAndOtherField(
    @RequestPart json: JsonDto,
    @RequestPart file: MultipartFile,
  ) {
    assertNotNull(json.a)
    assertNotNull(json.b)
    assertNotNull(file)
    assertFalse { file.isEmpty }
  }
}
