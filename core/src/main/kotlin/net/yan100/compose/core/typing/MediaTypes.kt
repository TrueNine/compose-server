/*
 *  Copyright (c) 2020-2024 TrueNine. All rights reserved.
 *
 * The following source code is owned, developed and copyrighted by TrueNine
 * (truenine304520@gmail.com) and represents a substantial investment of time, effort,
 * and resources. This software and its components are not to be used, reproduced,
 * distributed, or sublicensed in any form without the express written consent of
 * the copyright owner, except as permitted by law.
 * Any unauthorized use, distribution, or modification of this source code,
 * or any portion thereof, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * For inquiries regarding usage or redistribution, please contact:
 *     TrueNine
 *     email: <truenine304520@gmail.com>
 *     website: <github.com/TrueNine>
 */
package net.yan100.compose.core.typing

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue

/**
 * mime类型
 *
 * @author TrueNine
 * @since 2022-11-03
 */
enum class MediaTypes(private val extension: String, vararg m: String) : StringTyping {
  EXE("exe", "application/ms-download", "application/octet-stream"),

  /** 这个比较特殊，他的后缀名 是 binary 注意 */
  BINARY("binary", "application/octet-stream"),
  PNG("png", "image/png"),
  JPEG("jpg", "image/jpg", "image/jpeg"),
  BMP("bmp", "image/bmp"),
  WEBP("bmp", "image/webp"),
  GIF("gif", "image/gif"),
  TEXT("txt", "text/plain"),
  PDF("pdf", "application/pdf"),
  WORD("doc", "application/msword"),
  EXCEL("xls", "application/vnd.ms-excel"),
  PPTX("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
  XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
  DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
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
  SSE("sse", "text/event-stream");

  @JsonIgnore private var mm: Array<out String> = m

  @get:JsonIgnore
  @Suppress("UNCHECKED_CAST")
  val medias: Array<String>
    get() = mm as Array<String>

  @get:JsonIgnore
  val ext: String
    get() = extension

  @Deprecated("请改用标准化接口", ReplaceWith("getValue()"))
  fun media(): String {
    return this.value
  }

  @JsonValue override val value: String = this.mm[0]

  companion object {
    fun findVal(media: String): MediaTypes? {
      return entries.find { v -> v.medias.contains(media) }
    }
  }
}
