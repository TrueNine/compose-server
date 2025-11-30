package io.github.truenine.composeserver.rds.flywaymigrationshared.autoconfig

import io.github.truenine.composeserver.rds.flywaymigrationshared.TestEntrance
import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import jakarta.annotation.Resource
import org.springframework.boot.flyway.autoconfigure.FlywayProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.jdbc.core.JdbcTemplate
import kotlin.test.*

@SpringBootTest(classes = [TestEntrance::class])
class FlywayPropertiesOverrideAutoConfigurationTest : IDatabasePostgresqlContainer {
  @Resource lateinit var ctx: ApplicationContext

  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @Test
  fun `flyway properties overrides should work correctly`() {

    // Verify flyway-related tables exist
    val flywayTableCount =
      jdbcTemplate.queryForObject(
        """
        select count(*) from information_schema.tables 
        where table_name = 'flyway_schema_history'
        """
          .trimIndent(),
        Int::class.java,
      )
    assertEquals(1, flywayTableCount, "flyway_schema_history table was not created")

    // Verify test_user_account_table exists
    val userTableCount =
      jdbcTemplate.queryForObject(
        """
        select count(*) from information_schema.tables 
        where table_name = 'test_user_account_table'
        """
          .trimIndent(),
        Int::class.java,
      )
    assertEquals(1, userTableCount, "test_user_account_table was not created")
  }

  @Test
  fun `flyway properties enabled should be overridden`() {
    val flywayProperties = ctx.getBean(FlywayProperties::class.java)

    assertTrue("flyway properties 'enabled' property was not overridden") { flywayProperties.isEnabled }
    assertTrue { flywayProperties.isBaselineOnMigrate }
    assertTrue { flywayProperties.isOutOfOrder }
    assertEquals("0", flywayProperties.baselineVersion)
  }
}
