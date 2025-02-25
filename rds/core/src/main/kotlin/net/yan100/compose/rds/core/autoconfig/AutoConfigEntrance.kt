package net.yan100.compose.rds.core.autoconfig

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan

@EntityScan("net.yan100.compose.rds.core.entities.jpa")
@ComponentScan("net.yan100.compose.rds.core.listeners")
@ComponentScan("net.yan100.compose.rds.core.autoconfig")
class AutoConfigEntrance
