package com.truenine.component.rds.event

import org.springframework.context.ApplicationEvent

class TableRowChangeSpringEvent(
  model: Any
) : ApplicationEvent(model)
