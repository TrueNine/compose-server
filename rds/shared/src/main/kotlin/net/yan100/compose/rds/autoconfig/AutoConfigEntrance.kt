package net.yan100.compose.rds.autoconfig

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan

@EntityScan("net.yan100.compose.rds.entities.jpa")
@ComponentScan("net.yan100.compose.rds.listeners")
@ComponentScan("net.yan100.compose.rds.autoconfig")
class AutoConfigEntrance
