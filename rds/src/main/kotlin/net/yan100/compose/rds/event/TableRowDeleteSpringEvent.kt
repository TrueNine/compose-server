package net.yan100.compose.rds.event

import org.springframework.context.ApplicationEvent

class TableRowDeleteSpringEvent(
  model: Any
) : ApplicationEvent(model)
