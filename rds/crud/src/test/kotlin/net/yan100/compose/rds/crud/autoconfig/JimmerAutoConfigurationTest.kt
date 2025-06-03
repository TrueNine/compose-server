package net.yan100.compose.rds.crud.autoconfig

import jakarta.annotation.Resource
import net.yan100.compose.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import org.babyfish.jimmer.sql.meta.DatabaseNamingStrategy
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringBootTest
class JimmerAutoConfigurationTest : IDatabasePostgresqlContainer {
  @Resource(name = "lowercaseDatabaseNamingStrategy")
  lateinit var namingStrategy: DatabaseNamingStrategy

  @Test
  fun `ensure lowercase bean registered`() {
    assertNotNull(namingStrategy)
  }
}
