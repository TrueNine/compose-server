package io.github.truenine.composeserver.typing

import com.fasterxml.jackson.annotation.JsonValue
import io.github.truenine.composeserver.IStringTyping

/**
 * mime类型
 *
 * @author TrueNine
 * @since 2022-11-03
 */
enum class MediaTypes(private val extensions: Array<String>, private val mediaTypes: Array<String>) : IStringTyping {
  EXE(arrayOf("exe"), arrayOf("application/ms-download", "application/octet-stream")),

  /** 这个比较特殊，他的后缀名 是 binary 注意 */
  BINARY(arrayOf("binary"), arrayOf("application/octet-stream")),
  URL(arrayOf(), arrayOf("application/x-www-form-urlencoded")),
  MULTIPART_FORM_DATA(arrayOf(), arrayOf("multipart/form-data")),
  PNG(arrayOf("png"), arrayOf("image/png")),
  JPEG(arrayOf("jpeg", "jpg", "jpe", "jfif"), arrayOf("image/jpeg", "image/jpg")),
  BMP(arrayOf("bmp"), arrayOf("image/bmp")),
  WEBP(arrayOf("webp"), arrayOf("image/webp")),
  GIF(arrayOf("gif"), arrayOf("image/gif")),
  TEXT(arrayOf("txt", "text"), arrayOf("text/plain")),
  PDF(arrayOf("pdf"), arrayOf("application/pdf")),
  WORD(arrayOf("doc"), arrayOf("application/msword")),
  EXCEL(arrayOf("xls"), arrayOf("application/vnd.ms-excel")),
  PPTX(arrayOf("pptx"), arrayOf("application/vnd.openxmlformats-officedocument.presentationml.presentation")),
  XLSX(arrayOf("xlsx"), arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
  DOCX(arrayOf("docx"), arrayOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document")),
  PPT(arrayOf("ppt"), arrayOf("application/vnd.ms-powerpoint")),
  JSON(arrayOf("json"), arrayOf("application/json")),
  HTML(arrayOf("html", "htm"), arrayOf("text/html")),
  XML(arrayOf("xml"), arrayOf("text/xml", "application/xml")),
  JAVASCRIPT(arrayOf("js", "mjs"), arrayOf("text/javascript", "application/javascript")),
  CSS(arrayOf("css"), arrayOf("text/css")),
  OGG(arrayOf("ogg", "oga"), arrayOf("audio/ogg")),
  MP3(arrayOf("mp3"), arrayOf("audio/mpeg")),
  WAV(arrayOf("wav"), arrayOf("audio/x-wav", "audio/wav")),
  M3U(arrayOf("m3u", "m3u8"), arrayOf("audio/x-mpegurl")),
  M4A(arrayOf("m4a"), arrayOf("audio/x-m4a")),
  MP4(arrayOf("mp4"), arrayOf("video/mp4")),
  WEBM(arrayOf("webm"), arrayOf("video/webm")),
  JAR(arrayOf("jar"), arrayOf("application/java-archive")),
  ZIP(arrayOf("zip"), arrayOf("application/zip")),
  GZIP(arrayOf("gzip", "gz"), arrayOf("application/x-gzip", "application/gzip")),
  TAR(arrayOf("tar"), arrayOf("application/x-tar")),
  RAR(arrayOf("rar"), arrayOf("application/x-rar-compressed")),
  SSE(arrayOf(), arrayOf("text/event-stream"));

  val medias: Array<String>
    get() = mediaTypes

  val ext: String?
    get() = extensions.firstOrNull()

  val exts: Array<String>
    get() = extensions

  @Deprecated("请改用标准化接口", ReplaceWith("getValue()")) fun media(): String = value

  @JsonValue override val value: String = mediaTypes[0]

  companion object {
    @JvmStatic fun findVal(media: String?): MediaTypes? = entries.find { v -> if (media.isNullOrBlank()) false else media in v.medias }

    @JvmStatic fun findByExtension(extension: String?): MediaTypes? = entries.find { v -> if (extension.isNullOrBlank()) false else extension in v.extensions }

    @JvmStatic operator fun get(v: String?): MediaTypes? = findVal(v)
  }
}
