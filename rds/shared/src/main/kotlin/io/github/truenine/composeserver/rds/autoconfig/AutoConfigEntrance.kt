package io.github.truenine.composeserver.rds.autoconfig

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan

@EntityScan("io.github.truenine.composeserver.rds.entities.jpa")
@ComponentScan("io.github.truenine.composeserver.rds.listeners")
@ComponentScan("io.github.truenine.composeserver.rds.autoconfig")
class AutoConfigEntrance
