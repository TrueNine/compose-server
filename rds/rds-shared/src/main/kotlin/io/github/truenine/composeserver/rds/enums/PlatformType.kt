package io.github.truenine.composeserver.rds.enums

import io.github.truenine.composeserver.IStringEnum
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType
import org.babyfish.jimmer.sql.EnumType.Strategy

/**
 * # Platform types for different endpoints
 *
 * @author TrueNine
 * @since 2025-02-26
 */
@EnumType(Strategy.NAME)
enum class PlatformType(v: String) : IStringEnum {
  /** Unknown */
  @EnumItem(name = "NONE") @Deprecated(message = "In general, please specify the platform explicitly") NONE("NONE"),

  /** WeChat Official Account */
  @EnumItem(name = "WECHAT_PUBLIC_ACCOUNT") WECHAT_PUBLIC_ACCOUNT("WECHAT_PUBLIC_ACCOUNT"),

  /** WeChat Mini Program */
  @EnumItem(name = "WECHAT_MINI_PROGRAM") WECHAT_MINI_PROGRAM("WECHAT_MINI_PROGRAM"),

  /** WeChat Open Platform */
  @EnumItem(name = "WECHAT_OPEN_PLATFORM") WECHAT_OPEN_PLATFORM("WECHAT_OPEN_PLATFORM"),

  /** Mobile web view */
  @EnumItem(name = "MOBILE_WEB_VIEW") MOBILE_WEB_VIEW("MOBILE_WEB_VIEW"),

  /** Mobile H5 */
  @EnumItem(name = "MOBILE_H5") MOBILE_H5("MOBILE_H5"),

  /** WeChat web page */
  @EnumItem(name = "WECHAT_WEB_SITE") WECHAT_WEB_SITE("WECHAT_WEB_SITE"),

  /** PC website */
  @EnumItem(name = "PC_WEB_SITE") PC_WEB_SITE("PC_WEB_SITE"),

  /** Web admin site */
  @EnumItem(name = "WEB_ADMIN_SITE") WEB_ADMIN_SITE("WEB_ADMIN_SITE"),

  /** Other */
  @EnumItem(name = "OTHER") @Deprecated(message = "If the platform is unclear, please clarify it first") OTHER("OTHER");

  override val value = v

  companion object {

    @JvmStatic operator fun get(v: String?) = entries.find { it.value == v }
  }
}
