package net.yan100.compose.typing

/**
 * mime类型
 *
 * @author TrueNine
 * @since 2022-11-03
 */
enum class MimeTypes(private val extension: String, vararg m: String) :
  StringTyping {
  EXE("exe", "application/ms-download", "application/octet-stream"),

  /** 这个比较特殊，他的后缀名 是 binary 注意 */
  BINARY("binary", "application/octet-stream"),
  URL("", "application/x-www-form-urlencoded"),
  MULTIPART_FORM_DATA("", "multipart/form-data"),
  PNG("png", "image/png"),
  JPEG("jpg", "image/jpg", "image/jpeg"),
  BMP("bmp", "image/bmp"),
  WEBP("bmp", "image/webp"),
  GIF("gif", "image/gif"),
  TEXT("txt", "text/plain"),
  PDF("pdf", "application/pdf"),
  WORD("doc", "application/msword"),
  EXCEL("xls", "application/vnd.ms-excel"),
  PPTX(
    "pptx",
    "application/vnd.openxmlformats-officedocument.presentationml.presentation",
  ),
  XLSX(
    "xlsx",
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
  ),
  DOCX(
    "docx",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
  ),
  PPT("ppt", "application/vnd.ms-powerpoint"),
  JSON("json", "application/json"),
  HTML("html", "text/html"),
  XML("xml", "text/xml"),
  JAVASCRIPT("js", "text/javascript"),
  CSS("css", "text/css"),
  OGG("ogg", "audio/ogg"),
  MP3("mp3", "audio/mpeg"),
  WAV("wav", "audio/x-wav"),
  M3U("m3u", "audio/x-mpegurl"),
  M4A("mp4", "audio/x-m4a"),
  MP4("mp4", "video/mp4"),
  WEBM("webm", "video/webm"),
  JAR("jar", "application/java-archive"),
  ZIP("zip", "application/zip"),
  GZIP("gzip", "application/x-gzip"),
  TAR("tar", "application/x-tar"),
  RAR("rar", "application/x-rar-compressed"),
  SSE("", "text/event-stream");

  private var mm: Array<out String> = m

  @Suppress("UNCHECKED_CAST")
  val medias: Array<String>
    get() = mm as Array<String>

  val ext: String
    get() = extension

  @Deprecated("请改用标准化接口", ReplaceWith("getValue()"))
  fun media(): String {
    return this.value
  }

  override val value: String = this.mm[0]

  companion object {
    @JvmStatic
    fun findVal(media: String?): MimeTypes? =
      entries.find { v ->
        if (media.isNullOrBlank()) false else v.medias.contains(media)
      }

    @JvmStatic operator fun get(v: String?): MimeTypes? = findVal(v)
  }
}
