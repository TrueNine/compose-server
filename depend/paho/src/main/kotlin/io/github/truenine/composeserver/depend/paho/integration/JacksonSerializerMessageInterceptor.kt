package io.github.truenine.composeserver.depend.paho.integration

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.GenericMessage
import org.springframework.stereotype.Service

@Service
class JacksonSerializerMessageInterceptor(private val objectMapper: ObjectMapper) : ChannelInterceptor {
  override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
    val m =
      if (message.payload is String || message.payload is ByteArray) message
      else GenericMessage(objectMapper.writeValueAsString(message.payload), message.headers)
    return super.preSend(m, channel)
  }
}
