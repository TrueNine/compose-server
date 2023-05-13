package net.yan100.compose.rds.event

import org.springframework.context.ApplicationEvent

class TableRowChangeSpringEvent(
  model: Any
) : ApplicationEvent(model)
