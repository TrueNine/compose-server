package io.github.truenine.composeserver.rds.crud

import org.babyfish.jimmer.spring.repository.EnableJimmerRepositories
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan

@SpringBootApplication
@EntityScan(basePackages = ["io.github.truenine.composeserver.rds.crud.transaction"])
@EnableJimmerRepositories(basePackages = ["io.github.truenine.composeserver.rds.crud.repositories"])
internal class TestEntrance
