package net.yan100.compose.depend.servlet.annotations

import org.springframework.core.annotation.AliasFor
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.Mapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * ## HTTP Method HEAD
 *
 * 方法常用于检索资源的元数据，不响应 Body
 * - 应返回空数据，即使有数据也应当抛弃
 * - 应在请求头中体现，采取尽量少的资源索取策略，不应使用与 HTTP 无关的响应头
 * - see also [RFC HTTP protocol Method HEAD](https://www.rfc-editor.org/rfc/rfc7231#section-4.3.2)
 * - see also [MDN HTTP HEAD Method](https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Methods/HEAD)
 *
 * @see org.springframework.web.bind.annotation.GetMapping
 * @author TrueNine
 * @since 2024-11-22
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Mapping
@MustBeDocumented
@RequestMapping(method = [RequestMethod.HEAD], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
annotation class HeadMapping(
  @get:AliasFor(annotation = RequestMapping::class, attribute = "value")
  vararg val value: String = [],

  @get:AliasFor(annotation = RequestMapping::class, attribute = "path")
  val path: Array<String> = [],

  @get:AliasFor(annotation = RequestMapping::class, attribute = "name")
  val name: String = "",

  @get:AliasFor(annotation = RequestMapping::class) val params: Array<String> = [], @get:AliasFor(annotation = RequestMapping::class, attribute = "headers")
  val headers: Array<String> = [],

  @get:AliasFor(annotation = RequestMapping::class, attribute = "consumes")
  val consumes: Array<String> = [],

  @get:AliasFor(annotation = RequestMapping::class, attribute = "produces")
  val produces: Array<String> = []
)


