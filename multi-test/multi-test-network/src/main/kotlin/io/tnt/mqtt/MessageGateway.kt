package io.tnt.mqtt

import org.springframework.integration.annotation.MessagingGateway
import org.springframework.integration.mqtt.support.MqttHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestHeader

@Service
@MessagingGateway(defaultRequestChannel = "outBoundChannel")
interface MessageGateway {
  fun sendMessageToMqtt(data: String)
  fun sendMessageToMqtt(data: String, @Header(MqttHeaders.TOPIC) topic: String)
}
