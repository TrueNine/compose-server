package net.yan100.compose.rds.converters


import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.rds.RdsEntrance
import net.yan100.compose.rds.entities.DbTestPeriodConverterEntity
import net.yan100.compose.rds.repositories.DbTestPeriodConverterRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest(classes = [RdsEntrance::class])
class AesEncryptConverterTest {

  @Autowired
  private lateinit var repo: DbTestPeriodConverterRepository
  private val log = slf4j(this::class)

  @Test
  fun bootConverter() {
    repo.save(
      DbTestPeriodConverterEntity()
    )
  }
}
