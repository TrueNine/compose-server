package net.yan100.compose.oss

class FileArgs {
  lateinit var dir: String
  lateinit var fileName: String
  lateinit var mimeType: String
  var size: Long = 0L
  val sizeStr: String = size.toString()

  companion object {
    @JvmStatic fun builder(): FileArgsBuilder = FileArgsBuilder()

    @JvmStatic
    fun useStreamMap(map: StreamsMap): FileArgs {
      return builder()
        .dir(map.dirName)
        .fileName(map.fName)
        .mimeType(map.mediaType)
        .size(map.size)
        .build()
    }
  }
}
