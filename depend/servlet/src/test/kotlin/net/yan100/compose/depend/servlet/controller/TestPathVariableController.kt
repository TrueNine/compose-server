package net.yan100.compose.depend.servlet.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("test/pathVariable")
class TestPathVariableController {
  @GetMapping("urlencoded/{enc}")
  fun urlencoded(@PathVariable enc: String): String {
    return enc
  }
}
