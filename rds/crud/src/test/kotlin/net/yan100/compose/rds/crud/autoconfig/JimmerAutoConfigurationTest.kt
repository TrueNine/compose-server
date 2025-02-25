package net.yan100.compose.rds.crud.autoconfig

import jakarta.annotation.Resource
import kotlin.test.Test
import kotlin.test.assertNotNull
import org.babyfish.jimmer.sql.meta.DatabaseNamingStrategy
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class JimmerAutoConfigurationTest {
  lateinit var namingStrategy: DatabaseNamingStrategy
    @Resource(name = "lowercaseDatabaseNamingStrategy") set

  @Test
  fun `ensure lowercase bean registered`() {
    assertNotNull(namingStrategy)
  }
}
