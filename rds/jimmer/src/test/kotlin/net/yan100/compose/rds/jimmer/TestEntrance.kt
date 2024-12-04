package net.yan100.compose.rds.jimmer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories


@EnableJdbcRepositories(
  basePackages = [
    "net.yan100.compose.rds.jimmer.entities"
  ]
)
@SpringBootApplication
internal class TestEntrance {
  fun main(args: Array<String>) {
    runApplication<TestEntrance>(*args)
  }
}
