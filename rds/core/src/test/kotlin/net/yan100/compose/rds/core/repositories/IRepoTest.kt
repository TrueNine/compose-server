package net.yan100.compose.rds.core.repositories

import jakarta.annotation.Resource
import net.yan100.compose.rds.core.entities.TestIEntity
import net.yan100.compose.testtookit.log


class IRepoTest {

  lateinit var repo: TestIEntityRepo @Resource set


  fun `query persistence audit data`() {
    val saved = repo.save(TestIEntity())
    log.error("saved entity: {}", saved)
  }
}
