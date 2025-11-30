package io.github.truenine.composeserver.rds.flywaymigrationmysql8

import org.flywaydb.core.Flyway
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.flyway.autoconfigure.FlywayMigrationInitializer
import org.springframework.context.annotation.*
import javax.sql.DataSource

@Configuration
@ConditionalOnClass(Flyway::class)
class TestFlywayConfiguration {

  @Bean
  @Primary
  fun testFlywayMigrationInitializer(flyway: Flyway, dataSource: DataSource): FlywayMigrationInitializer {
    return object : FlywayMigrationInitializer(flyway) {
      override fun afterPropertiesSet() {
        try {
          // Try to repair first to handle failed migrations
          flyway.repair()
        } catch (_: Exception) {
          // If repair fails, clean and try again
          try {
            flyway.clean()
          } catch (_: Exception) {
            // If clean fails, ignore and proceed
          }
        }
        // Now proceed with normal migration
        super.afterPropertiesSet()
      }
    }
  }
}
