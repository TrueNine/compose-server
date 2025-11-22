package io.github.truenine.composeserver.rds.autoconfig

import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.ComponentScan

@EntityScan("io.github.truenine.composeserver.rds.entities.jpa")
@ComponentScan("io.github.truenine.composeserver.rds.listeners")
@ComponentScan("io.github.truenine.composeserver.rds.autoconfig")
class AutoConfigEntrance
