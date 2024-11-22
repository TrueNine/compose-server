package net.yan100.compose.depend.servlet.controller

import net.yan100.compose.depend.servlet.annotations.HeadMapping
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("test/head")
class HeadController {

  @HeadMapping("a")
  fun a(): ResponseEntity<Unit> {
    return ResponseEntity.status(200)
      .build()
  }
}
