package com.truenine.component.rds.autoconfig

import com.truenine.component.rds.listener.TableRowDeleteApplicationListener
import com.truenine.component.core.properties.SnowflakeProperties
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
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
    "com.truenine.component.rds.models",
    "com.truenine.component.rds.base"
  ]
)
@EnableConfigurationProperties(SnowflakeProperties::class)
@Import(TableRowDeleteApplicationListener::class)
class AutoConfigEntrance
