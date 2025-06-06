package net.yan100.compose.rds

import org.babyfish.jimmer.spring.repository.EnableJimmerRepositories
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan

@SpringBootApplication
@EntityScan(basePackages = ["net.yan100.compose.rds.entities"])
@EnableJimmerRepositories("net.yan100.compose.rds.repositories")
internal class TestEntrance
