package net.yan100.compose.rds.core

import org.babyfish.jimmer.spring.repository.EnableJimmerRepositories
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaAuditing
@SpringBootApplication
@EntityScan(
  basePackages = [
    "net.yan100.compose.rds.core.entities"
  ]
)
@EnableJpaRepositories("net.yan100.compose.rds.core.repositories")
@EnableJimmerRepositories("net.yan100.compose.rds.core.repositories.jimmer")
internal class TestEntrance
