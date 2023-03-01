package io.tn.core.exceptions


/**
 * 账号密码未找到，账号或密码错误
 *
 * @author TrueNine
 * @since 2022-12-25
 */
open class AccountOrPasswordErrorException(msg: String? = "账号或密码未找到") :
  BasicBizException(msg)
