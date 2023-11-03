package net.yan100.compose.rds.core.event

import org.springframework.context.ApplicationEvent

class TableRowDeleteSpringEvent(
  model: Any
) : ApplicationEvent(model)
