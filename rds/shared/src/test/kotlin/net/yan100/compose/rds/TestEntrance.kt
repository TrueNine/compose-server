package net.yan100.compose.rds

import org.babyfish.jimmer.spring.repository.EnableJimmerRepositories
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaAuditing
@SpringBootApplication
@EntityScan(basePackages = ["net.yan100.compose.rds.entities"])
@EnableJpaRepositories("net.yan100.compose.rds.repositories")
@EnableJimmerRepositories("net.yan100.compose.rds.repositories.jimmer")
internal class TestEntrance
