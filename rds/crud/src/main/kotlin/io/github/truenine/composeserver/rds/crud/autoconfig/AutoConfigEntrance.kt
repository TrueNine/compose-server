package io.github.truenine.composeserver.rds.crud.autoconfig

import org.babyfish.jimmer.spring.repository.EnableJimmerRepositories
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan

@EntityScan(basePackages = ["io.github.truenine.composeserver.rds.crud.entities"])
@EnableJimmerRepositories(basePackages = ["io.github.truenine.composeserver.rds.crud.repositories"])
@ComponentScan(
  "io.github.truenine.composeserver.rds.crud.autoconfig",
  "io.github.truenine.composeserver.rds.crud.converters",
  "io.github.truenine.composeserver.rds.crud.service",
)
class AutoConfigEntrance
