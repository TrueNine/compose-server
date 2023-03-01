package io.tn.rds.autoconfig

import io.tn.rds.event.DelListener
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@ComponentScan(
  "io.tn.rds.autoconfig",
  "io.tn.rds.service"
)
@EnableJpaRepositories(
  "io.tn.rds.repo",
)
@EnableJpaAuditing
@EntityScan(
  basePackages = [
    "io.tn.rds.dao",
    "io.tn.rds.dto",
    "io.tn.rds.vo",
    "io.tn.rds.base"
  ]
)
@Import(DelListener::class)
class AutoConfigEntrance
