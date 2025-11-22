package io.github.truenine.composeserver.rds

import org.babyfish.jimmer.spring.repository.EnableJimmerRepositories
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan

@SpringBootApplication
@EntityScan(basePackages = ["io.github.truenine.composeserver.rds.entities"])
@EnableJimmerRepositories("io.github.truenine.composeserver.rds.repositories")
internal class TestEntrance
