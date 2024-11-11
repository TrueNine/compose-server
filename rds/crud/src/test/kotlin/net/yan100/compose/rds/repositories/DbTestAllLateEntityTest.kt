package net.yan100.compose.rds.repositories

import jakarta.annotation.Resource
import net.yan100.compose.rds.entities.DbTestAllLateEntity
import net.yan100.compose.testtookit.log
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import kotlin.test.*

/**
 * # 此测试保证
 *
 * 1. ksp 生成的 entity 是可以正常生成
 * 2. 基础类型所做的代理操作是正确的
 * 3. 可以正确被 spring 和 hibernate 识别
 * 4. 保证在最小限度的字节码
 */
@Rollback
@SpringBootTest
class DbTestAllLateEntityTest {
  lateinit var repo: IDbTestAllLateEntityRepo @Resource set

  @BeforeTest
  fun setup() {
    assertNotNull(repo)
    repo.save(DbTestAllLateEntity().apply {
      a = 1
      b = 1.1
      c = "a"
      d = true
    })
  }

  @Test
  fun `launch repo`() {
    val all = repo.findAll()
    log.info("all data: {}", all)

    assertTrue { all.isNotEmpty() }
    assertEquals(1, all.size)

    val first = all[0]
    assertNotNull(first)

    assertEquals(1, first.a)
    assertEquals(1.1, first.b)
    assertEquals("a", first.c)
    assertTrue(first.d)
  }
}
