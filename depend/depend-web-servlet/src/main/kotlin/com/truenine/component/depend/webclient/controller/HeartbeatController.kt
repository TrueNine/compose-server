package com.truenine.component.depend.webclient.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "服务器心跳接口")
@RestController
@RequestMapping("v1/heartbeat")
class HeartbeatController {

  @Operation(summary = "用于检测服务器是否存活")
  @GetMapping("ping")
  fun pong(): Int = 1
}
