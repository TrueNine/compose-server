package io.github.truenine.composeserver.consts

interface IMethods {
  companion object {
    fun all(): Array<String> {
      return arrayOf(GET, POST, PUT, DELETE, OPTIONS, PATCH, HEAD, TRACE)
    }

    const val GET: String = "GET"
    const val POST: String = "POST"
    const val PUT: String = "PUT"
    const val DELETE: String = "DELETE"
    const val OPTIONS: String = "OPTIONS"
    const val PATCH: String = "PATCH"
    const val HEAD: String = "HEAD"
    const val TRACE: String = "TRACE"
  }
}
