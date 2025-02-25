package net.yan100.compose.rds.crud

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories(
  basePackages = ["net.yan100.compose.rds.crud.repositories.ksp"]
)
@SpringBootApplication
internal class TestEntrance
