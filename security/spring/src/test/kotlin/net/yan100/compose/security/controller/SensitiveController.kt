package net.yan100.compose.security.controller

import net.yan100.compose.core.Pr
import net.yan100.compose.core.annotations.SensitiveResponse
import net.yan100.compose.core.domain.ISensitivity
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
