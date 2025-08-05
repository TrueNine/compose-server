package io.github.truenine.composeserver.rds.flywaymigrationmysql8

import javax.sql.DataSource
import org.flywaydb.core.Flyway
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

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
        } catch (e: Exception) {
          // If repair fails, clean and try again
          try {
            flyway.clean()
          } catch (cleanException: Exception) {
            // If clean fails, ignore and proceed
          }
        }
        // Now proceed with normal migration
        super.afterPropertiesSet()
      }
    }
  }
}
