package io.github.truenine.composeserver.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.security.spring.security.SecurityExceptionAdware
import io.github.truenine.composeserver.slf4j

private val log = slf4j(EmptySecurityExceptionAdware::class)

class EmptySecurityExceptionAdware(mapper: ObjectMapper? = null) : SecurityExceptionAdware(mapper) {
  init {
    log.warn("正在使用默认安全异常处理器，生产环请自行覆写")
  }
}
