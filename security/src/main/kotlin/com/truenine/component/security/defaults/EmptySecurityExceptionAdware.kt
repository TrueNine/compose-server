package com.truenine.component.security.defaults

import com.truenine.component.core.lang.EmptyDefaultModel
import com.truenine.component.core.lang.LogKt
import com.truenine.component.security.spring.security.SecurityExceptionAdware


class EmptySecurityExceptionAdware : EmptyDefaultModel, SecurityExceptionAdware() {
  private val log = LogKt.getLog(this::class)

  init {
    log.trace("正在使用默认安全异常处理器，生产环请自行重写")
  }
}
