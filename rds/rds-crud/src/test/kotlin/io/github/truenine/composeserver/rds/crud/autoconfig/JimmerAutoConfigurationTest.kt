package io.github.truenine.composeserver.rds.crud.autoconfig

import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import jakarta.annotation.Resource
import org.babyfish.jimmer.sql.meta.DatabaseNamingStrategy
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@SpringBootTest
class JimmerAutoConfigurationTest : IDatabasePostgresqlContainer {
  @Resource(name = "lowercaseDatabaseNamingStrategy") lateinit var namingStrategy: DatabaseNamingStrategy

  @Test
  fun `ensure lowercase bean registered`() {
    assertNotNull(namingStrategy)
  }
}
