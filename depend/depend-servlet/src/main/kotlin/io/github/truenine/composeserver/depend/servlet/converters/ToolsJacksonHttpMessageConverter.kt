package io.github.truenine.composeserver.depend.servlet.converters

import java.lang.reflect.Type
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpOutputMessage
import org.springframework.http.MediaType
import org.springframework.http.converter.AbstractHttpMessageConverter
import org.springframework.http.converter.GenericHttpMessageConverter
import tools.jackson.databind.ObjectMapper

/** Jackson HTTP message converter that bridges Spring MVC with the relocated `tools.jackson` ObjectMapper. */
class ToolsJacksonHttpMessageConverter(private val objectMapper: ObjectMapper) :
  AbstractHttpMessageConverter<Any>(*SUPPORTED_MEDIA_TYPES), GenericHttpMessageConverter<Any> {

  override fun getDefaultCharset(): Charset = StandardCharsets.UTF_8

  override fun supports(clazz: Class<*>): Boolean = !isExcludedType(clazz)

  override fun canRead(mediaType: MediaType?): Boolean {
    if (mediaType == null) {
      return true
    }
    if (mediaType.subtype.endsWith("+json", ignoreCase = true)) {
      return true
    }
    return super.canRead(mediaType)
  }

  override fun canWrite(mediaType: MediaType?): Boolean {
    if (mediaType == null) {
      return true
    }
    if (mediaType.subtype.endsWith("+json", ignoreCase = true)) {
      return true
    }
    return super.canWrite(mediaType)
  }

  override fun readInternal(clazz: Class<out Any>, inputMessage: HttpInputMessage): Any {
    inputMessage.body.use { stream ->
      return objectMapper.readValue(stream, clazz)
    }
  }

  override fun read(type: Type, contextClass: Class<*>?, inputMessage: HttpInputMessage): Any {
    val javaType = objectMapper.typeFactory.constructType(type)
    inputMessage.body.use { stream ->
      return objectMapper.readValue(stream, javaType)
    }
  }

  override fun canRead(type: Type, contextClass: Class<*>?, mediaType: MediaType?): Boolean {
    if (type is Class<*> && !supports(type)) {
      return false
    }
    return canRead(mediaType)
  }

  override fun canWrite(type: Type?, clazz: Class<*>, mediaType: MediaType?): Boolean {
    if (!supports(clazz)) {
      return false
    }
    return canWrite(mediaType)
  }

  override fun writeInternal(t: Any, outputMessage: HttpOutputMessage) {
    outputMessage.body.use { stream -> objectMapper.writeValue(stream, t) }
  }

  override fun write(t: Any, type: Type?, mediaType: MediaType?, outputMessage: HttpOutputMessage) {
    if (mediaType != null) {
      outputMessage.headers.contentType = mediaType
    } else if (outputMessage.headers.contentType == null) {
      outputMessage.headers.contentType = MediaType.APPLICATION_JSON
    }
    writeInternal(t, outputMessage)
  }

  companion object {
    private val SUPPORTED_MEDIA_TYPES = arrayOf(MediaType.APPLICATION_JSON, MediaType.APPLICATION_PROBLEM_JSON, MediaType.APPLICATION_NDJSON)
  }
}

private fun isExcludedType(clazz: Class<*>?): Boolean {
  if (clazz == null) {
    return false
  }
  if (CharSequence::class.java.isAssignableFrom(clazz)) {
    return true
  }
  if (clazz == ByteArray::class.java) {
    return true
  }
  return false
}
