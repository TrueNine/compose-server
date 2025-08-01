package io.github.truenine.composeserver.rds.flywaymigrationshared.autoconfig

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.flyway.FlywayProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class FlywayPropertiesOverrideAutoConfiguration {

  @Bean
  @Primary
  fun flywayProperties(
    @Value("\${spring.flyway.baseline-version:#{null}}") baselineVersion: String?,
    @Value("\${spring.flyway.enabled:#{true}}") enabled: Boolean?,
  ): FlywayProperties {
    val p = FlywayProperties()
    if (enabled == false) {
      return p
    }

    p.baselineVersion = baselineVersion ?: "9000"

    p.isEnabled = true
    p.isBaselineOnMigrate = true
    p.isOutOfOrder = true
    p.locations = listOf("classpath:db/migration")

    return p
  }
}
