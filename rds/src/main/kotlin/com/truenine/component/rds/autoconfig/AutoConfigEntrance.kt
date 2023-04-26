package com.truenine.component.rds.autoconfig

import com.truenine.component.core.properties.SnowflakeProperties
import com.truenine.component.rds.listener.TableRowDeleteSpringListener
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@EnableJpaAuditing
@EntityScan(
  basePackages = [
    "com.truenine.component.rds.entity",
    "com.truenine.component.rds.models",
    "com.truenine.component.rds.base"
  ]
)
@ComponentScan(
  "com.truenine.component.rds.autoconfig",
  "com.truenine.component.rds.converters",
  "com.truenine.component.rds.service",
  "com.truenine.component.rds.repository",
  "com.truenine.component.rds.base",
  "com.truenine.component.rds.listener"
)
@EnableJpaRepositories(
  "com.truenine.component.rds.repository",
)
@EnableConfigurationProperties(SnowflakeProperties::class)
class AutoConfigEntrance
