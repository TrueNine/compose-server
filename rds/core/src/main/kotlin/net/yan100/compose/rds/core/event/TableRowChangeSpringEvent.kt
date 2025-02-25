package net.yan100.compose.rds.core.event

import org.springframework.context.ApplicationEvent

class TableRowChangeSpringEvent(model: Any) : ApplicationEvent(model)
