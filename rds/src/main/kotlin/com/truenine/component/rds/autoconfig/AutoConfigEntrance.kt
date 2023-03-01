package com.truenine.component.rds.autoconfig

import com.truenine.component.rds.event.DelListener
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@ComponentScan(
  "com.truenine.component.rds.autoconfig",
  "com.truenine.component.rds.service"
)
@EnableJpaRepositories(
  "com.truenine.component.rds.repo",
)
@EnableJpaAuditing
@EntityScan(
  basePackages = [
    "com.truenine.component.rds.dao",
    "com.truenine.component.rds.dto",
    "com.truenine.component.rds.vo",
    "com.truenine.component.rds.base"
  ]
)
@Import(DelListener::class)
class AutoConfigEntrance
