package net.yan100.compose.client.ts

import net.yan100.compose.meta.annotations.client.Api
import org.springframework.web.bind.annotation.*

@Api
@RestController
@RequestMapping("v1/tsHttpMethod")
class TsHttpMethodController {
  /**
   * 返回 null
   * @param a 请求参数
   */
  @Api
  @GetMapping("methodRequestMapping")
  fun methodRequestMapping(a: String? = "abc"): Map<String, List<Map<String, Boolean>>>? {
    return null
  }

  @Api
  @RequestMapping(method = [RequestMethod.HEAD])
  fun methodHead() {

  }

  @Api
  @RequestMapping("{a}/{b}", method = [RequestMethod.GET, RequestMethod.POST])
  fun inputPathVariable(
    @PathVariable a: String,
    @PathVariable b: String
  ) {

  }
}
