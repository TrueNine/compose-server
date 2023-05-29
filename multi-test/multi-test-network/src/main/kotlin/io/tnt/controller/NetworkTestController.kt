package io.tnt.controller

import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*

open class RequestParams {
  open var a: String? = null
  open var b: String? = null
  open var c: Int? = null
  open var d: List<String> = listOf()
}

@RestController
@RequestMapping("v1/net")
class NetworkTestController {

  @Operation(summary = "无RequestParam", description = "没有 @RequestMParam，全参数为 string")
  @GetMapping("get1")
  fun get1(a: String, b: String): List<Any> {
    return listOf<Any>(a, b)
  }

  @Operation(summary = "无RequestParam，混合参数", description = "没有 @RequestMParam，全参数为 string int")
  @GetMapping("get2")
  fun get2(a: String, b: Int): List<Any> {
    return listOf(a, b)
  }

  @Operation(summary = "包含RequestParam", description = "包含 @RequestMParam，string int")
  @GetMapping("get3")
  fun get3(@RequestParam("a") a: String, @RequestParam("b") b: Int): List<Any> {
    return listOf(a, b)
  }


  @Operation(summary = "不包含 @RequestBody", description = "不包含 @RequestBody")
  @PostMapping("post1")
  fun post1(p: RequestParams): RequestParams {
    return p
  }

  @Operation(summary = "包含 @RequestBody", description = "包含 @RequestBody")
  @PostMapping("post2")
  fun post2(@RequestBody p: RequestParams): RequestParams {
    return p
  }

  @Operation(summary = "包含 @RequestParam", description = "包含 @RequestParam")
  @PostMapping("post3")
  fun post3(@RequestParam a: String, @RequestParam b: String, @RequestParam c: Int, @RequestParam d: List<String>): RequestParams {
    return RequestParams().apply {
      this.a = a
      this.b = b
      this.c = c
      this.d = d
    }
  }
}
