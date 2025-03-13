package net.yan100.compose.depend.servlet

import net.yan100.compose.core.domain.IReadableAttachment
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
