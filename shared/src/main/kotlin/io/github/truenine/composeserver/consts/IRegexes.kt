package io.github.truenine.composeserver.consts

/**
 * Common regular expression constants.
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
     * Chinese administrative division code format.
     *
     * Supported lengths:
     * - 2 digits
     * - 4 digits
     * - 6 digits
     * - 9 digits
     * - 12 digits
     */
    const val CHINA_AD_CODE = "(?:[1-9]\\d{1}|[1-9]\\d{3}|[1-9]\\d{5}|[1-9]\\d{8}|[1-9]\\d{11})\$"

    /** Cache / other configuration key */
    const val CONFIG_KEY = "^(?![.])[a-zA-Z0-9_.]+$"

    /** Chinese ID card number */
    const val CHINA_ID_CARD = "${CHINA_ID_CARD_PREFIX}$"

    /** Chinese disability certificate number */
    const val CHINA_DIS_CARD = "${CHINA_ID_CARD_PREFIX}[1-7][1-4](?:[bB][1-9])?$"

    /** Alphanumeric account */
    const val ACCOUNT: String = "^[a-zA-Z0-9]+$"

    /** Password with digits, letters and special characters */
    const val PASSWORD: String = "^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[^\\da-zA-Z\\s]).{1,9}$"

    /** Chinese mainland mobile phone number */
    const val CHINA_PHONE: String = "^1[3-9][0-9]\\d{8}$"

    /**
     * ## Ant-style URI
     * 1. Must start with `/` and only contain `/` as separators
     * 2. Must not contain `..` or `./`
     */
    const val ANT_URI = "^/(?!.*\\.\\.+)(?!.*:)(?!.*%)(?!.*\\.\\.+)(?!.*\\/\\/+)(?!.*\\s)(?:[\\w\\-\\+\\.]+|\\*)(?:/(?:[\\w\\-\\+\\.]+|\\*))*\$|^/\$"
    const val IP_V4 = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$"
    const val BASE_64 = "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$"
    const val SH1 = "^[0-9a-f]{40}\$"
    const val RBAC_NAME = "^([a-zA-Z]){1}|([a-zA-Z][a-zA-Z0-9_\\-:]*[a-zA-Z0-9])\$"
  }
}
