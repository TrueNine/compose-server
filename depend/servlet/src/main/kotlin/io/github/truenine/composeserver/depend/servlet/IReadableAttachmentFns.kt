package io.github.truenine.composeserver.depend.servlet

import io.github.truenine.composeserver.domain.IReadableAttachment
import org.springframework.web.multipart.MultipartFile

fun MultipartFile.toReadableAttachment(): IReadableAttachment {
  return IReadableAttachment.DefaultReadableAttachment(
    name = this.originalFilename ?: this.name,
    mimeType = this.contentType,
    size = this.size,
    bytes = { this.bytes },
    inputStream = this.inputStream,
    empty = this.isEmpty,
  )
}
