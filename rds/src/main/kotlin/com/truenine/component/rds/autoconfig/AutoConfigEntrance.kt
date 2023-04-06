package com.truenine.component.rds.autoconfig

import com.truenine.component.core.properties.SnowflakeProperties
import com.truenine.component.rds.converters.AesEncryptConverter
import com.truenine.component.rds.converters.PointModelConverter
import com.truenine.component.rds.listener.TableRowDeleteApplicationListener
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@EnableJpaAuditing
@EntityScan(
  basePackages = [
    "com.truenine.component.rds.dao",
    "com.truenine.component.rds.models",
    "com.truenine.component.rds.base"
  ]
)
@ComponentScan(
  "com.truenine.component.rds.autoconfig",
  "com.truenine.component.rds.service",
  "com.truenine.component.rds.repo",
  "com.truenine.component.rds.base"
)
@EnableJpaRepositories(
  "com.truenine.component.rds.repo",
)
@Import(
  value = [
    TableRowDeleteApplicationListener::class,
    AesEncryptConverter::class,
    PointModelConverter::class,
  ]
)
@EnableConfigurationProperties(SnowflakeProperties::class)
class AutoConfigEntrance
