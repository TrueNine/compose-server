package net.yan100.compose.oss.common

import net.yan100.compose.core.i64
import net.yan100.compose.core.typing.MimeTypes

/**
 * ## oss bucket -> object 参数表示
 * @author TrueNine
 * @since 2025-03-13
 *
 * @param bucketName bucket 名称
 * @param objectName object 名称
 * @param size object 大小
 * @param contentTypeFor object mimeType
 * @param contentType 媒体类型
 * @param headers object 头部信息
 */
data class ObjectArgs(
  val objectName: String,
  val bucketName: String,
  val size: i64,
  private val contentTypeFor: MimeTypes? = MimeTypes.BINARY,
  val contentType: String = contentTypeFor?.value ?: MimeTypes.BINARY.value,
  val headers: Map<String, String> = emptyMap(),
)
