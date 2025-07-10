package net.yan100.compose.rds.flywaymigrationpostgresql.autoconfig

import jakarta.annotation.Resource
import kotlin.test.Test
import net.yan100.compose.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import org.springframework.boot.autoconfigure.flyway.FlywayProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestPropertySource

@SpringBootTest(properties = ["spring.config.name=application-flyway-disabled"])
@TestPropertySource(locations = ["classpath:application-flyway-disabled.yaml"])
class FlywayPropertiesOverrideAutoConfigurationDisabledTest : IDatabasePostgresqlContainer {
  @Resource lateinit var ctx: ApplicationContext

  @Test
  fun `当 enabled=false 时，FlywayProperties 应保持默认值`() {
    val flywayProperties = ctx.getBean(FlywayProperties::class.java)
    // enabled 应为 false
    kotlin.test.assertEquals(false, flywayProperties.isEnabled)
    // baselineVersion 应为 null（未被覆盖）
    kotlin.test.assertEquals("1", flywayProperties.baselineVersion)
    // 其他属性也应为默认值
    kotlin.test.assertEquals(false, flywayProperties.isBaselineOnMigrate)
    kotlin.test.assertEquals(false, flywayProperties.isOutOfOrder)
    kotlin.test.assertEquals(listOf("classpath:db/migration"), flywayProperties.locations)
  }
}
