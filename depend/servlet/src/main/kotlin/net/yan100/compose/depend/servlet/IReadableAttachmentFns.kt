package net.yan100.compose.depend.servlet

import net.yan100.compose.core.domain.IReadableAttachment
import net.yan100.compose.core.typing.MimeTypes
import org.springframework.web.multipart.MultipartFile

fun MultipartFile.toReadableAttachment(): IReadableAttachment {
  return IReadableAttachment[
    originalFilename ?: name,
    contentType ?: MimeTypes.BINARY.name,
    isEmpty,
    size,
    bytes,
    inputStream
  ]
}
