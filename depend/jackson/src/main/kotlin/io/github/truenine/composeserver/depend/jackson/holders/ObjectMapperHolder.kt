package io.github.truenine.composeserver.depend.jackson.holders

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.holders.AbstractThreadLocalHolder

object ObjectMapperHolder : AbstractThreadLocalHolder<ObjectMapper>()
