package io.tnt.controller

import io.tnt.mqtt.MessageGateway
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("mqtt")
class MqttController(
  private val gateway: MessageGateway
) {

  @GetMapping("send")
  fun send() {
    gateway.sendMessageToMqtt("abc","23333")
  }
}
