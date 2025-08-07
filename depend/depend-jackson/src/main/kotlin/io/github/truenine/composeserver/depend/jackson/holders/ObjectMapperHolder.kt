package io.github.truenine.composeserver.depend.jackson.holders

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.truenine.composeserver.holders.AbstractThreadLocalHolder

object ObjectMapperHolder : AbstractThreadLocalHolder<ObjectMapper>()
