package net.yan100.compose.rds.crud.autoconfig

import org.babyfish.jimmer.spring.repository.EnableJimmerRepositories
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EntityScan(
  basePackages =
    [
      "net.yan100.compose.rds.crud.entities",
      "net.yan100.compose.rds.core.entities",
    ]
)
@EnableJpaRepositories(
  basePackages = ["net.yan100.compose.rds.crud.repositories.jpa"]
)
@EnableJimmerRepositories(
  basePackages = ["net.yan100.compose.rds.crud.repositories.jimmer"]
)
@ComponentScan(
  "net.yan100.compose.rds.crud.autoconfig",
  "net.yan100.compose.rds.crud.converters",
  "net.yan100.compose.rds.crud.service",
  "net.yan100.compose.rds.crud.listener",
)
class AutoConfigEntrance
