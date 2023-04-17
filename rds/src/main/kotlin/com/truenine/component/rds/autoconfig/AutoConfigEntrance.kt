package com.truenine.component.rds.autoconfig

import cn.hutool.core.convert.impl.DurationConverter
import cn.hutool.core.convert.impl.PeriodConverter
import com.truenine.component.core.properties.SnowflakeProperties
import com.truenine.component.rds.converters.AesEncryptConverter
import com.truenine.component.rds.converters.PointModelConverter
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
  "com.truenine.component.rds.service",
  "com.truenine.component.rds.repository",
  "com.truenine.component.rds.base"
)
@EnableJpaRepositories(
  "com.truenine.component.rds.repository",
)
@Import(
  value = [
    TableRowDeleteSpringListener::class,
    AesEncryptConverter::class,
    DurationConverter::class,
    PeriodConverter::class,
    PointModelConverter::class,
  ]
)
@EnableConfigurationProperties(SnowflakeProperties::class)
class AutoConfigEntrance
