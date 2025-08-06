package io.github.truenine.composeserver.depend.servlet

import io.github.truenine.composeserver.datetime
import io.github.truenine.composeserver.toMillis
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

data class ResponseEntityScope(
  internal var status: Int = 200,
  internal var contentType: String? = null,
  internal var lastModifier: Long? = null,
  internal var contentLength: Long? = null,
) {

  fun type(contentType: String?) {
    this.contentType = contentType
  }

  fun size(len: () -> Long?) {
    contentLength = len()
  }

  fun lastModifyBy(dt: () -> datetime?) {
    lastModifier = dt()?.toMillis()
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
inline fun headMethodResponse(scope: ResponseEntityScope.() -> Unit): ResponseEntity<Unit> {
  val sc = ResponseEntityScope()
  scope(sc)
  return sc.build()
}
