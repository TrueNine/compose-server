package com.truenine.component.core.exceptions

/**
 * 基础业务异常
 *
 * @author TrueNine
 * @since 2023-02-19
 */
open class BasicBizException(msg: String? = "用户逻辑异常") :
  RuntimeException(msg)
