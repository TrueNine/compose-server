package net.yan100.compose.rds.converters


import net.yan100.compose.core.lang.slf4j
import net.yan100.compose.rds.RdsEntrance
import net.yan100.compose.rds.entity.DbTestPeriodConverterEntity
import net.yan100.compose.rds.repository.DbTestPeriodConverterRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test


@SpringBootTest(classes = [RdsEntrance::class])
class AesEncryptConverterTest : AbstractTestNGSpringContextTests() {

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
