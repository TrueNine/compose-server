package io.github.truenine.composeserver.oss

@Deprecated("不建议使用 builder")
class FileArgs {
  lateinit var dir: String
  lateinit var fileName: String
  lateinit var mimeType: String
  var size: Long = 0L

  companion object {
    @JvmStatic fun builder(): FileArgsBuilder = FileArgsBuilder()
  }
}
