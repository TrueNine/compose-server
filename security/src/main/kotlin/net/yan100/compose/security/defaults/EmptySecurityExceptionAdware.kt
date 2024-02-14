package net.yan100.compose.security.defaults

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.lang.EmptyDefault
import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.security.spring.security.SecurityExceptionAdware


class EmptySecurityExceptionAdware(
    mapper: ObjectMapper
) : EmptyDefault, SecurityExceptionAdware(mapper) {
    companion object {
        @JvmStatic
        private val log = slf4j(EmptySecurityExceptionAdware::class)
    }

    init {
        log.warn("正在使用默认安全异常处理器，生产环请自行覆写")
    }
}
