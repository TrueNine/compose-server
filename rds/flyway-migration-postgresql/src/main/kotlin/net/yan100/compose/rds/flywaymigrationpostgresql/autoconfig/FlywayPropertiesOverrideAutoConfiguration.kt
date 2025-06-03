package net.yan100.compose.rds.flywaymigrationpostgresql.autoconfig

import org.springframework.boot.autoconfigure.flyway.FlywayProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class FlywayPropertiesOverrideAutoConfiguration {

  @Bean
  @Primary
  fun flywayProperties(): FlywayProperties {
    val p = FlywayProperties()

    p.isEnabled = true
    p.baselineVersion = "9000"
    p.isBaselineOnMigrate = true
    p.isOutOfOrder = true
    p.locations = listOf("classpath:db/migration")

    return p
  }
}
