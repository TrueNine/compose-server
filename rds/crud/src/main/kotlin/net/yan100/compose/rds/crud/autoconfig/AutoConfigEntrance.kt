package net.yan100.compose.rds.crud.autoconfig

import org.babyfish.jimmer.spring.repository.EnableJimmerRepositories
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan

@EntityScan(basePackages = ["net.yan100.compose.rds.crud.entities"])
@EnableJimmerRepositories(basePackages = ["net.yan100.compose.rds.crud.repositories"])
@ComponentScan("net.yan100.compose.rds.crud.autoconfig", "net.yan100.compose.rds.crud.converters", "net.yan100.compose.rds.crud.service")
class AutoConfigEntrance
