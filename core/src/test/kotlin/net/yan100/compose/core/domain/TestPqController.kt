package net.yan100.compose.core.domain

import net.yan100.compose.core.Pq
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("v1/pq")
class TestPqController {
  @GetMapping("get/default")
  fun inputPq(pq: Pq?): Pq? {
    return pq
  }

  @GetMapping("get/requestBody")
  fun getRequestBody(@RequestBody pq: Pq?): Pq? {
    return pq
  }
}
