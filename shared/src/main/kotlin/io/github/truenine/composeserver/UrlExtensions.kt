package io.github.truenine.composeserver

import io.github.truenine.composeserver.holders.optimization.PerformanceOptimizer
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * URL building and manipulation utilities with performance optimizations
 *
 * These utilities provide efficient URL construction and manipulation, particularly useful for object storage operations and web service integrations.
 *
 * @author TrueNine
 * @since 2025-08-04
 */

/**
 * Build a URL path by joining segments with proper separators
 *
 * @param segments The path segments to join
 * @return The constructed URL path with optimized string interning
 */
fun buildUrlPath(vararg segments: String): String {
  if (segments.isEmpty()) return "/"

  val cleanSegments = segments.filter { it.isNotBlank() }.map { it.trim('/') }.filter { it.isNotEmpty() }

  if (cleanSegments.isEmpty()) return "/"

  val path = "/" + cleanSegments.joinToString("/")
  return PerformanceOptimizer.internString(path)
}

/**
 * Build a complete URL from base URL and path segments
 *
 * @param baseUrl The base URL
 * @param segments The path segments to append
 * @return The complete URL with optimized string interning
 */
fun buildUrl(baseUrl: String, vararg segments: String): String {
  val cleanBaseUrl = baseUrl.trimEnd('/')
  val path = buildUrlPath(*segments)
  val url = if (path == "/") cleanBaseUrl else "$cleanBaseUrl$path"
  return PerformanceOptimizer.internString(url)
}

/**
 * Build a public object URL for OSS services
 *
 * @param baseUrl The base URL of the OSS service
 * @param bucketName The bucket name
 * @param objectName The object name
 * @return The complete public URL
 */
fun buildObjectUrl(baseUrl: String, bucketName: String, objectName: String): String {
  return buildUrl(baseUrl, bucketName, objectName)
}

/**
 * URL encode a string using UTF-8 encoding
 *
 * @param value The string to encode
 * @return The URL encoded string
 */
fun String.urlEncode(): String {
  return URLEncoder.encode(this, StandardCharsets.UTF_8)
}

/**
 * URL encode path segments while preserving path separators
 *
 * @param path The path to encode
 * @return The encoded path with preserved separators
 */
fun String.urlEncodePath(): String {
  return this.split("/").joinToString("/") { segment -> if (segment.isBlank()) segment else segment.urlEncode() }
}

/**
 * Build query string from parameters map
 *
 * @param params The parameters map
 * @param includeEmpty Whether to include parameters with empty values
 * @return The query string (without leading '?')
 */
fun buildQueryString(params: Map<String, String>, includeEmpty: Boolean = false): String {
  if (params.isEmpty()) return STR_EMPTY

  val queryParams = params.filter { (_, value) -> includeEmpty || value.isNotBlank() }.map { (key, value) -> "${key.urlEncode()}=${value.urlEncode()}" }

  return if (queryParams.isEmpty()) {
    STR_EMPTY
  } else {
    queryParams.joinToString("&")
  }
}

/**
 * Build a complete URL with query parameters
 *
 * @param baseUrl The base URL
 * @param pathSegments The path segments
 * @param queryParams The query parameters
 * @return The complete URL with query string
 */
fun buildUrlWithQuery(baseUrl: String, pathSegments: Array<String> = emptyArray(), queryParams: Map<String, String> = emptyMap()): String {
  val url = buildUrl(baseUrl, *pathSegments)
  val queryString = buildQueryString(queryParams)

  return if (queryString.isEmpty()) {
    url
  } else {
    PerformanceOptimizer.internString("$url?$queryString")
  }
}

/**
 * Extract the file extension from a URL or file path
 *
 * @param url The URL or file path
 * @return The file extension (without dot) or empty string if none
 */
fun extractFileExtension(url: String): String {
  val lastSlash = url.lastIndexOf('/')
  val lastDot = url.lastIndexOf('.')
  val lastQuestion = url.lastIndexOf('?')

  // If there's a query string, ignore it
  val effectiveEnd = if (lastQuestion > lastDot) lastQuestion else url.length

  return if (lastDot > lastSlash && lastDot < effectiveEnd - 1) {
    url.substring(lastDot + 1, effectiveEnd).lowercase()
  } else {
    STR_EMPTY
  }
}

/**
 * Normalize a URL by removing redundant slashes and segments
 *
 * @param url The URL to normalize
 * @return The normalized URL
 */
fun normalizeUrl(url: String): String {
  if (url.isBlank()) return url

  // Split into protocol, host, and path parts
  val protocolEnd = url.indexOf("://")
  if (protocolEnd == -1) {
    // No protocol, treat as path only
    return normalizePath(url)
  }

  val protocol = url.substring(0, protocolEnd + 3)
  val remaining = url.substring(protocolEnd + 3)

  val pathStart = remaining.indexOf('/')
  if (pathStart == -1) {
    // No path, just protocol and host
    return PerformanceOptimizer.internString(url)
  }

  val host = remaining.substring(0, pathStart)
  val path = remaining.substring(pathStart)

  val normalizedPath = normalizePath(path)
  return PerformanceOptimizer.internString("$protocol$host$normalizedPath")
}

/**
 * Normalize a path by removing redundant slashes and resolving . and .. segments
 *
 * @param path The path to normalize
 * @return The normalized path
 */
private fun normalizePath(path: String): String {
  if (path.isBlank()) return "/"

  val segments = path.split("/").filter { it.isNotEmpty() && it != "." }
  val normalizedSegments = mutableListOf<String>()

  for (segment in segments) {
    when (segment) {
      ".." -> if (normalizedSegments.isNotEmpty()) normalizedSegments.removeLastOrNull()
      else -> normalizedSegments.add(segment)
    }
  }

  val result =
    if (normalizedSegments.isEmpty()) {
      "/"
    } else {
      "/" + normalizedSegments.joinToString("/")
    }

  return PerformanceOptimizer.internString(result)
}
