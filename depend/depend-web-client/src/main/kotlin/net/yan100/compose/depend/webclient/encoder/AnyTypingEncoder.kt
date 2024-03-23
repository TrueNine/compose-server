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
package net.yan100.compose.depend.webclient.encoder

import net.yan100.compose.core.typing.AnyTyping
import org.reactivestreams.Publisher
import org.springframework.core.ResolvableType
import org.springframework.core.codec.AbstractEncoder
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.util.MimeType
import reactor.core.publisher.Flux

class AnyTypingEncoder : AbstractEncoder<AnyTyping>() {
  override fun encode(
    inputStream: Publisher<out AnyTyping>,
    bufferFactory: DataBufferFactory,
    elementType: ResolvableType,
    mimeType: MimeType?,
    hints: MutableMap<String, Any>?,
  ): Flux<DataBuffer> {
    return Flux.from(inputStream).map { bufferFactory.wrap(it.toString().toByteArray()) }
  }
}
