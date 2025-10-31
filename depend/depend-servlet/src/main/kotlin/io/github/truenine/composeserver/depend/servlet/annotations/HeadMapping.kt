package io.github.truenine.composeserver.depend.servlet.annotations

import org.springframework.core.annotation.AliasFor
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.Mapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

/**
 * ## HTTP Method HEAD
 *
 * This method is often used to retrieve metadata for a resource without returning the response body.
 * - Should return empty data; any data present should be discarded.
 * - Should be reflected in the request headers, adopting a minimal resource retrieval strategy.
 *   Response headers unrelated to HTTP should not be used.
 * - See also [RFC HTTP protocol Method HEAD](https://www.rfc-editor.org/rfc/rfc7231#section-4.3.2)
 * - See also [MDN HTTP HEAD Method](https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods/HEAD)
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
  @get:AliasFor(annotation = RequestMapping::class, attribute = "value") vararg val value: String = [],
  @get:AliasFor(annotation = RequestMapping::class, attribute = "path") val path: Array<String> = [],
  @get:AliasFor(annotation = RequestMapping::class, attribute = "name") val name: String = "",
  @get:AliasFor(annotation = RequestMapping::class) val params: Array<String> = [],
  @get:AliasFor(annotation = RequestMapping::class, attribute = "headers") val headers: Array<String> = [],
  @get:AliasFor(annotation = RequestMapping::class, attribute = "consumes") val consumes: Array<String> = [],
  @get:AliasFor(annotation = RequestMapping::class, attribute = "produces") val produces: Array<String> = [],
)
