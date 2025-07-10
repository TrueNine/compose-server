package io.github.truenine.composeserver.oss

import io.github.truenine.composeserver.i64
import io.github.truenine.composeserver.typing.MimeTypes

/**
 * ## oss bucket -> object 参数表示
 *
 * @param bucketName bucket 名称
 * @param objectName object 名称
 * @param size object 大小
 * @param contentTypeFor object mimeType
 * @param contentType 媒体类型
 * @param headers object 头部信息
 * @author TrueNine
 * @since 2025-03-13
 */
data class ObjectArgs(
  val objectName: String,
  val bucketName: String,
  val size: i64,
  private val contentTypeFor: MimeTypes? = MimeTypes.BINARY,
  val contentType: String = contentTypeFor?.value ?: MimeTypes.BINARY.value,
  val headers: Map<String, String> = emptyMap(),
)
