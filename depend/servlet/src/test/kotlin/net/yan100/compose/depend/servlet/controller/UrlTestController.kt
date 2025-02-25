package net.yan100.compose.depend.servlet.controller

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("test/url")
class UrlTestController {
  @GetMapping("query", produces = [MediaType.APPLICATION_JSON_VALUE])
  fun getQuery(@RequestParam("a") a: MutableList<Int>): MutableList<Int> {
    return a
  }
}
