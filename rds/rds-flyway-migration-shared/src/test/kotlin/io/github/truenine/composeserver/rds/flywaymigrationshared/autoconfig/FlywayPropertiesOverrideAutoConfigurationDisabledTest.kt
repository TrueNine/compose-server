package io.github.truenine.composeserver.rds.flywaymigrationshared.autoconfig

import io.github.truenine.composeserver.rds.flywaymigrationshared.TestEntrance
import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import jakarta.annotation.Resource
import org.springframework.boot.flyway.autoconfigure.FlywayProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestPropertySource
import kotlin.test.Test

@SpringBootTest(classes = [TestEntrance::class], properties = ["spring.config.name=application-flyway-disabled"])
@TestPropertySource(locations = ["classpath:application-flyway-disabled.yaml"])
class FlywayPropertiesOverrideAutoConfigurationDisabledTest : IDatabasePostgresqlContainer {
  @Resource lateinit var ctx: ApplicationContext

  @Test
  fun `when enabled is false FlywayProperties should keep default values`() {
    val flywayProperties = ctx.getBean(FlywayProperties::class.java)
    // enabled should be false
    kotlin.test.assertEquals(false, flywayProperties.isEnabled)
    // baselineVersion should keep its default value (not overridden)
    kotlin.test.assertEquals("1", flywayProperties.baselineVersion)
    // Other properties should also keep their default values
    kotlin.test.assertEquals(false, flywayProperties.isBaselineOnMigrate)
    kotlin.test.assertEquals(false, flywayProperties.isOutOfOrder)
    kotlin.test.assertEquals(listOf("classpath:db/migration"), flywayProperties.locations)
  }
}
