package io.github.truenine.composeserver.rds.typing

import io.github.truenine.composeserver.typing.StringTyping
import org.babyfish.jimmer.sql.EnumItem
import org.babyfish.jimmer.sql.EnumType
import org.babyfish.jimmer.sql.EnumType.Strategy

/**
 * # 各端口平台类型
 *
 * @author TrueNine
 * @since 2025-02-26
 */
@EnumType(Strategy.NAME)
enum class PlatformType(v: String) : StringTyping {
  /** 未知 */
  @EnumItem(name = "NONE") @Deprecated(message = "一般情况，请明确指定平台") NONE("NONE"),

  /** 微信公众号 */
  @EnumItem(name = "WECHAT_PUBLIC_ACCOUNT") WECHAT_PUBLIC_ACCOUNT("WECHAT_PUBLIC_ACCOUNT"),

  /** 微信小程序 */
  @EnumItem(name = "WECHAT_MINI_PROGRAM") WECHAT_MINI_PROGRAM("WECHAT_MINI_PROGRAM"),

  /** 微信开放平台 */
  @EnumItem(name = "WECHAT_OPEN_PLATFORM") WECHAT_OPEN_PLATFORM("WECHAT_OPEN_PLATFORM"),

  /** 移动端 veb view */
  @EnumItem(name = "MOBILE_WEB_VIEW") MOBILE_WEB_VIEW("MOBILE_WEB_VIEW"),

  /** 移动端 h5 */
  @EnumItem(name = "MOBILE_H5") MOBILE_H5("MOBILE_H5"),

  /** 微信网页 */
  @EnumItem(name = "WECHAT_WEB_SITE") WECHAT_WEB_SITE("WECHAT_WEB_SITE"),

  /** pc 网站 */
  @EnumItem(name = "PC_WEB_SITE") PC_WEB_SITE("PC_WEB_SITE"),

  /** web 后台管理端 */
  @EnumItem(name = "WEB_ADMIN_SITE") WEB_ADMIN_SITE("WEB_ADMIN_SITE"),

  /** 其他 */
  @EnumItem(name = "OTHER") @Deprecated(message = "如果不明确平台，请先明确") OTHER("OTHER");

  override val value = v

  companion object {

    @JvmStatic operator fun get(v: String?) = entries.find { it.value == v }
  }
}
