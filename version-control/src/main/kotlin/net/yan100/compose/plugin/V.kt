package net.yan100.compose.plugin


/**
 * 版本管理
 */
object V {
  const val COMPOSE_VERSION = ProjectVersion.VERSION

  object Compose {
    const val DATA_COMMON_DATA_EXTRACT = COMPOSE_VERSION
    const val DEPEND_FLYWAY = COMPOSE_VERSION
    const val CORE = COMPOSE_VERSION
    const val RDS = COMPOSE_VERSION
    const val SCHEDULE = COMPOSE_VERSION
    const val RDS_GEN = COMPOSE_VERSION
    const val WEB_API_DOC = COMPOSE_VERSION
    const val SECURITY = COMPOSE_VERSION
    const val SECURITY_OAUTH2 = COMPOSE_VERSION
    const val DEPEND_WEB_SERVLET = COMPOSE_VERSION
    const val DEPEND_WEB_CLIENT = COMPOSE_VERSION
    const val DATA_COMMON_CRAWLER = COMPOSE_VERSION
    const val CACHEABLE = COMPOSE_VERSION
    const val OSS = COMPOSE_VERSION
    const val PAY = COMPOSE_VERSION
  }

  val Crawler = net.yan100.compose.plugin.versions.Crawler
  val Lang = net.yan100.compose.plugin.versions.Lang
  val Db = net.yan100.compose.plugin.versions.Db
  val PlatformSdk = net.yan100.compose.plugin.versions.PlatformSdk
  val Util = net.yan100.compose.plugin.versions.Util
  val Web = net.yan100.compose.plugin.versions.Web
  val StandardEdition = net.yan100.compose.plugin.versions.StandardEdition
  val Test = net.yan100.compose.plugin.versions.Test
  val Spring = net.yan100.compose.plugin.versions.Spring
  val Plugin = net.yan100.compose.plugin.versions.Plugin
  val Security = net.yan100.compose.plugin.versions.Security
}
