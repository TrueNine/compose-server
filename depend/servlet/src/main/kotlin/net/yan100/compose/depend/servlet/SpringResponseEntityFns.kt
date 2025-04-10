package net.yan100.compose.depend.servlet

import net.yan100.compose.datetime
import net.yan100.compose.i32
import net.yan100.compose.i64
import net.yan100.compose.toLong
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

data class ResponseEntityScope(
  internal var status: i32 = 200,
  internal var contentType: String? = null,
  internal var lastModifier: i64? = null,
  internal var contentLength: i64? = null,
) {

  fun type(contentType: String?) {
    this.contentType = contentType
  }

  fun size(len: () -> i64?) {
    contentLength = len()
  }

  fun lastModifyBy(dt: () -> datetime?) {
    lastModifier = dt()?.toLong()
  }

  fun exists(sc: () -> Any?) {
    status =
      when (val result = sc()) {
        (result == null) -> 404
        is Boolean -> if (result) 200 else 404
        is Unit -> 404
        is Number -> if (result.toLong() >= 0) 200 else 404
        is Array<*> -> if (result.size > 0) 200 else 404
        is Iterable<*> -> if (result.iterator().hasNext()) 200 else 404
        is Map<*, *> -> if (result.size > 0) 200 else 404
        is String -> if (result.isNotEmpty()) 200 else 404
        is Any -> 200
        else -> 404
      }
  }

  fun build(): ResponseEntity<Unit> {
    val re =
      ResponseEntity.status(status)
        .also { if (lastModifier != null) it.lastModified(lastModifier!!) }
        .also { if (contentLength != null) it.contentLength(contentLength!!) }
        .also {
          if (contentType != null) {
            val media =
              try {
                MediaType.parseMediaType(contentType!!)
              } catch (e: Exception) {
                MediaType.APPLICATION_OCTET_STREAM
              }
            it.contentType(media)
          }
        }
        .body(Unit)

    return re
  }
}

/** 加以限制的 HTTP HEAD 返回 */
inline fun headMethodResponse(
  scope: ResponseEntityScope.() -> Unit,
): ResponseEntity<Unit> {
  val sc = ResponseEntityScope()
  scope(sc)
  return sc.build()
}
