package net.yan100.compose.rds.crud.service

import jakarta.annotation.Resource
import kotlin.test.Test
import net.yan100.compose.rds.crud.entities.jpa.UserInfo
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback

@Rollback
@SpringBootTest
class IMergeEntityEventServiceTest {
  lateinit var service: IUserInfoService
    @Resource set

  @Test
  fun `test merge fun 0`() {
    val from = service.post(UserInfo())
    val to = service.post(UserInfo())

    val merged = service.cascadeMerge(from, to)

    println(merged)
  }
}
