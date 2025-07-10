package io.github.truenine.composeserver.domain

import io.github.truenine.composeserver.Pq
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/pq")
class TestPqController {
  @GetMapping("get/default")
  fun inputPq(pq: Pq?): IPageParam {
    return Pq[pq?.o, pq?.s]
  }

  @GetMapping("get/requestBody")
  fun getRequestBody(@RequestBody pq: Pq?): Pq? {
    return pq
  }
}
