package io.github.truenine.composeserver.security.controller

import io.github.truenine.composeserver.Pr
import io.github.truenine.composeserver.annotations.SensitiveResponse
import io.github.truenine.composeserver.domain.ISensitivity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("test/sensitive")
class SensitiveController {
  class Resp(var a: Int = 1) : ISensitivity {
    override fun changeWithSensitiveData() {
      super.changeWithSensitiveData()
      this.a = 233
    }
  }

  @SensitiveResponse
  @GetMapping("get", produces = ["application/json"])
  fun `test get a`(): Pr<Resp> {
    return Pr[listOf(Resp(), Resp(), Resp())]
  }
}
