package io.github.truenine.composeserver.depend.servlet.controller

import io.github.truenine.composeserver.int
import io.github.truenine.composeserver.string
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("test/getParameter")
class TestGetParameterController {
  open class Dto {
    var name: string? = null
    var age: int? = null
  }

  data class DataClassDto(var name: String? = null, var age: Int? = null)

  @GetMapping("nonAnnotation") fun nonAnnotation(dto: Dto): Dto = dto

  @GetMapping("requestParam") fun requestParam(@RequestParam dto: Dto): Dto = dto

  @GetMapping("nonAnnotationDataClass") fun nonAnnotationDataClass(dto: DataClassDto) = dto

  @GetMapping("strList")
  fun inputStringList(@RequestParam list: List<String>): List<String> {
    return list
  }
}
