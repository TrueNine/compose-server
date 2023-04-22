package com.truenine.component.security.defaults

import com.fasterxml.jackson.databind.ObjectMapper
import com.truenine.component.core.lang.EmptyDefaultModel
import com.truenine.component.core.lang.LogKt
import com.truenine.component.security.spring.security.SecurityExceptionAdware


class EmptySecurityExceptionAdware(
  mapper: ObjectMapper
) : EmptyDefaultModel, SecurityExceptionAdware(mapper) {
  private val log = LogKt.getLog(this::class)

  init {
    log.warn("正在使用默认安全异常处理器，生产环请自行覆写")
  }
}
