package io.github.truenine.composeserver.depend.httpexchange.encoder

import io.github.truenine.composeserver.IAnyTyping
import org.reactivestreams.Publisher
import org.springframework.core.ResolvableType
import org.springframework.core.codec.AbstractEncoder
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.util.MimeType
import reactor.core.publisher.Flux

class AnyTypingEncoder : AbstractEncoder<IAnyTyping>() {
  override fun encode(
    inputStream: Publisher<out IAnyTyping>,
    bufferFactory: DataBufferFactory,
    elementType: ResolvableType,
    mimeType: MimeType?,
    hints: MutableMap<String, Any>?,
  ): Flux<DataBuffer> {
    return Flux.from(inputStream).map { bufferFactory.wrap(it.toString().toByteArray()) }
  }
}
