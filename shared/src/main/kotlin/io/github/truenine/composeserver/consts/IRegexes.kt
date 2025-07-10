package io.github.truenine.composeserver.consts

/**
 * 常用正则表达式常量
 *
 * @author TrueNine
 * @since 2023-04-19
 */
interface IRegexes {
  companion object {
    private const val ONE_ONE = "(1[1-9]|[2-9][0-9])"
    private const val ZERO_ONE = "(0[1-9]|[1-9][0-9])"
    private const val ZERO_ZERO_ZERO_ONE = "(00|0[1-9]|[1-9][0-9])"
    private const val YEAR = "(19|20)\\d{2}"
    private const val MONTH = "(0[1-9]|1[0-2])"
    private const val DAY = "(0[1-9]|[1-2][0-9]|3[0-1])"
    private const val CHINA_ID_CARD_PREFIX = "^${ONE_ONE}${ZERO_ZERO_ZERO_ONE}${ZERO_ONE}${YEAR}${MONTH}${DAY}\\d{3}[xX0-9]"

    /**
     * 中国行政区划代码 支持：
     * - 2位
     * - 4位
     * - 6位
     * - 9位
     * - 12位
     */
    const val CHINA_AD_CODE = "(?:[1-9]\\d{1}|[1-9]\\d{3}|[1-9]\\d{5}|[1-9]\\d{8}|[1-9]\\d{11})\$"

    /** 缓存 / 其他配置 key */
    const val CONFIG_KEY = "^(?![.])[a-zA-Z0-9_.]+$"

    /** 中国身份证号 */
    const val CHINA_ID_CARD = "${CHINA_ID_CARD_PREFIX}$"

    /** 中国残疾证号 */
    const val CHINA_DIS_CARD = "${CHINA_ID_CARD_PREFIX}[1-7][1-4](?:[bB][1-9])?$"

    /** 英文数字账户 */
    const val ACCOUNT: String = "^[a-zA-Z0-9]+$"

    /** 密码 */
    const val PASSWORD: String = "^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[^\\da-zA-Z\\s]).{1,9}$"

    /** 中国手机号手机 */
    const val CHINA_PHONE: String = "^1[3-9][0-9]\\d{8}$"

    /**
     * ## Ant 风格的 URI
     * 1. `/` 开头或 只能出现 `/`
     * 2. 不能出现 `..` `./`
     */
    const val ANT_URI = "^/(?!.*\\.\\.+)(?!.*:)(?!.*%)(?!.*\\.\\.+)(?!.*\\/\\/+)(?!.*\\s)(?:[\\w\\-\\+\\.]+|\\*)(?:/(?:[\\w\\-\\+\\.]+|\\*))*\$|^/\$"
    const val IP_V4 = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$"
    const val BASE_64 = "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$"
    const val SH1 = "^[0-9a-f]{40}\$"
    const val RBAC_NAME = "^([a-zA-Z]){1}|([a-zA-Z][a-zA-Z0-9_\\-:]*[a-zA-Z0-9])\$"
  }
}
