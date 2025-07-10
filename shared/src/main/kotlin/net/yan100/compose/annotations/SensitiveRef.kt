package net.yan100.compose.annotations

import net.yan100.compose.nonText

enum class SensitiveStrategy(private val desensitizeSerializer: (String) -> String) {
  /** ## 单个 * 纯掩码 */
  ONCE({ "*" }),

  /** 不进行脱敏处理 */
  NONE({ it }),

  /** 手机号 */
  PHONE({ it.replace("^(\\S{3})\\S+(\\S{2})$".toRegex(), "\$1****\$2") }),
  EMAIL({ it.replace("(\\S{2})\\S+(@[\\w.-]+)".toRegex(), "\$1****\$2") }),

  /** 身份证号 */
  ID_CARD({ it.replace("(\\S{2})\\S+(\\S{2})".toRegex(), "\$1****\$2") }),

  /** 银行卡号 */
  BANK_CARD_CODE({ it.replace("(\\w{2})\\w+(\\w{2})".toRegex(), "\$1****\$2") }),

  /** 姓名 */
  NAME({ if (it.nonText()) it else "**${it.substring(it.length - 1)}" }),

  /**
   * ## 多段落姓名
   *
   * 例如：`last_name`
   */
  MULTIPLE_NAME({
    if (it.nonText() || it.length <= 2) {
      when (it.length) {
        1 -> "*"
        2 -> "**"
        else -> it
      }
    } else "**${it.substring(it.length - 1)}"
  }),

  /** 地址 */
  ADDRESS({ it.replace("(\\S{3})\\S{2}(\\S*)\\S{2}".toRegex(), "\$1****\$2") }),

  /** 密码 */
  PASSWORD({ "****" });

  open fun desensitizeSerializer(): (String) -> String {
    return desensitizeSerializer
  }
}
