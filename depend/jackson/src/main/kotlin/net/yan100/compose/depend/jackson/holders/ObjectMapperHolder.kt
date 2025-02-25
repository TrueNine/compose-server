package net.yan100.compose.depend.jackson.holders

import com.fasterxml.jackson.databind.ObjectMapper
import net.yan100.compose.core.holders.AbstractThreadLocalHolder

object ObjectMapperHolder : AbstractThreadLocalHolder<ObjectMapper>()
