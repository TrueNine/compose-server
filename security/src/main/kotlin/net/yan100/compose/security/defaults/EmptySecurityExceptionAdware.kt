package net.yan100.compose.security.defaults

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.security.spring.security.SecurityExceptionAdware


class EmptySecurityExceptionAdware(
  mapper: ObjectMapper
) : net.yan100.compose.core.lang.EmptyDefaultModel, SecurityExceptionAdware(mapper) {
  private val log = slf4j(this::class)

  init {
    log.warn("正在使用默认安全异常处理器，生产环请自行覆写")
  }
}
