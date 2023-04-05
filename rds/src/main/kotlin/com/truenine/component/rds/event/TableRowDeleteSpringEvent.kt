package com.truenine.component.rds.event

import org.springframework.context.ApplicationEvent

class TableRowDeleteSpringEvent(
  model: Any
) : ApplicationEvent(model)
