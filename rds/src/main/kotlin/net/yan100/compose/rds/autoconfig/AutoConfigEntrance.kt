package net.yan100.compose.rds.autoconfig

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@EnableJpaAuditing
@EntityScan(
  basePackages = [
    "net.yan100.compose.rds.entity",
    "net.yan100.compose.rds.models",
    "net.yan100.compose.rds.base"
  ]
)
@ComponentScan(
  "net.yan100.compose.rds.autoconfig",
  "net.yan100.compose.rds.converters",
  "net.yan100.compose.rds.service",
  "net.yan100.compose.rds.repository",
  "net.yan100.compose.rds.base",
  "net.yan100.compose.rds.listener"
)
@EnableJpaRepositories(
  "net.yan100.compose.rds.repository",
)
class AutoConfigEntrance
