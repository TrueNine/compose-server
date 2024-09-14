/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.depend.paho.integration

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
