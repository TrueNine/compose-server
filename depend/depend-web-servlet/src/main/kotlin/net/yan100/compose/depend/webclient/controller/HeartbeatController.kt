package net.yan100.compose.depend.webclient.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import net.yan100.compose.core.http.InterAddressUtil
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.InterfaceAddress

@Tag(name = "服务器心跳接口")
@RestController
@RequestMapping("v1/heartbeat")
class HeartbeatController {

  @Operation(summary = "用于检测服务器是否存活")
  @GetMapping("ping")
  fun pong(): Int = 1

  @Operation(summary = "获取当前连接用户的 ip 地址")
  @GetMapping("ip")
  fun ip(req: HttpServletRequest): String? {
    val ip = InterAddressUtil.getRequestIpAddress(req)
    return null
  }
}
