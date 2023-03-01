package io.tn.rds.event

import org.springframework.context.ApplicationEvent

class DelEvent(
  delObj: Any
) : ApplicationEvent(delObj)
