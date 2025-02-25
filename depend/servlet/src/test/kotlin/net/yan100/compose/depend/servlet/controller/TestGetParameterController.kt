package net.yan100.compose.depend.servlet.controller

import net.yan100.compose.core.int
import net.yan100.compose.core.string
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("test/getParameter")
class TestGetParameterController {
  open class Dto {
    var name: string? = null
    var age: int? = null
  }

  data class DataClassDto(var name: String? = null, var age: Int? = null)

  @GetMapping("nonAnnotation") fun nonAnnotation(dto: Dto): Dto = dto

  @GetMapping("requestParam")
  fun requestParam(@RequestParam dto: Dto): Dto = dto

  @GetMapping("nonAnnotationDataClass")
  fun nonAnnotationDataClass(dto: DataClassDto) = dto

  @GetMapping("strList")
  fun inputStringList(@RequestParam list: List<String>): List<String> {
    return list
  }
}
