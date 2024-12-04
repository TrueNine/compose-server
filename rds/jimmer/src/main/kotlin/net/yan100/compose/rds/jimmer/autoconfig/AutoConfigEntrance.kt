package net.yan100.compose.rds.jimmer.autoconfig

import org.babyfish.jimmer.spring.repository.EnableJimmerRepositories
import org.springframework.context.annotation.ComponentScan

@EnableJimmerRepositories(
  basePackages = [
    "net.yan100.compose.rds.jimmer.repositories"
  ]
)
@ComponentScan(
  "net.yan100.compose.rds.jimmer.autoconfig"
)
class AutoConfigEntrance
