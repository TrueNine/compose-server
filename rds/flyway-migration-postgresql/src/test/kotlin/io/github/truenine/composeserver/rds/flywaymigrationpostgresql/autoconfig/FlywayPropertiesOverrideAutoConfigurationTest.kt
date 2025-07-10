package io.github.truenine.composeserver.rds.flywaymigrationpostgresql.autoconfig

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import jakarta.annotation.Resource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.springframework.boot.autoconfigure.flyway.FlywayProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class FlywayPropertiesOverrideAutoConfigurationTest : IDatabasePostgresqlContainer {
  @Resource lateinit var ctx: ApplicationContext

  @Resource lateinit var jdbcTemplate: JdbcTemplate

  @Test
  fun `检查 flyway properties 被覆盖后 是否工作正常`() {

    // 检查 flyway 相关表是否存在
    val flywayTableCount =
      jdbcTemplate.queryForObject(
        """
        SELECT COUNT(*) FROM information_schema.tables 
        WHERE table_name = 'flyway_schema_history'
      """
          .trimIndent(),
        Int::class.java,
      )
    assertEquals(1, flywayTableCount, "flyway_schema_history 表未创建")

    // 检查 test_user_account 表是否存在
    val userTableCount =
      jdbcTemplate.queryForObject(
        """
        SELECT COUNT(*) FROM information_schema.tables 
        WHERE table_name = 'test_user_account_table'
      """
          .trimIndent(),
        Int::class.java,
      )
    assertEquals(1, userTableCount, "test_user_account 表未创建")
  }

  @Test
  fun `确保 flyway properties 的 enabled 已经被覆盖`() {
    val flywayProperties = ctx.getBean(FlywayProperties::class.java)

    assertTrue("没有覆盖到 flyway properties 的 enabled 属性") { flywayProperties.isEnabled }
    assertTrue { flywayProperties.isBaselineOnMigrate }
    assertTrue { flywayProperties.isOutOfOrder }
    assertEquals("9000", flywayProperties.baselineVersion)
  }
}
