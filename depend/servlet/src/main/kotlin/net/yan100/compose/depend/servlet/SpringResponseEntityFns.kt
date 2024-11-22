package net.yan100.compose.depend.servlet

import net.yan100.compose.core.datetime
import net.yan100.compose.core.i32
import net.yan100.compose.core.i64
import net.yan100.compose.core.toLong
import org.springframework.http.ResponseEntity

class ResponseEntityScope {
  private val builder: ResponseEntity.BodyBuilder = ResponseEntity.ok()
  private var status: i32 = 200
  private var lastModifier: i64? = null
  private var contentLength: i64? = null

  init {
    builder.body(null)
  }

  fun size(len: () -> i64?) {
    contentLength = len()
  }

  fun lastModifyBy(dt: () -> datetime?) {
    lastModifier = dt()?.toLong()
  }

  fun exists(sc: () -> Any?) {
    status = when (val result = sc()) {
      (result == null) -> 404
      is Boolean -> if (result) 200 else 404
      is Unit -> 404
      is Number -> if (result.toInt() > 0) 200 else 404
      is Array<*> -> if (result.size > 0) 200 else 404
      is Iterable<*> -> if (result.iterator().hasNext()) 200 else 404
      is Map<*, *> -> if (result.size > 0) 200 else 404
      is String -> if (result.isNotEmpty()) 200 else 404
      is Any -> 200
      else -> 404
    }
  }

  fun build(): ResponseEntity<Unit> {
    if (contentLength == null) status = 404

    val re = ResponseEntity.status(status).also {
      if (lastModifier != null) it.lastModified(lastModifier!!)
    }.also {
      if (contentLength != null) it.contentLength(contentLength!!)
    }.body(Unit)

    return re
  }
}

/**
 * 加以限制的 HTTP HEAD 返回
 */
inline fun headMethodResponse(scope: ResponseEntityScope.() -> Unit): ResponseEntity<Unit> {
  val sc = ResponseEntityScope()
  scope(sc)
  return sc.build()
}
