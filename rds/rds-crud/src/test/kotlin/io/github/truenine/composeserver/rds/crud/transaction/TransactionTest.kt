package io.github.truenine.composeserver.rds.crud.transaction

import io.github.truenine.composeserver.testtoolkit.RDBRollback
import io.github.truenine.composeserver.testtoolkit.testcontainers.IDatabasePostgresqlContainer
import jakarta.annotation.Resource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.Transactional

@RDBRollback
@SpringBootTest
class TransactionTest : IDatabasePostgresqlContainer {
  @Resource private lateinit var testService: TestService
  @Resource private lateinit var jdbcTemplate: JdbcTemplate

  @BeforeEach
  fun setupTable() {
    // 手动创建测试表
    jdbcTemplate.execute(
      """
      CREATE TABLE IF NOT EXISTS test_entity (
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR DEFAULT NULL,
        value INTEGER DEFAULT NULL
      )
      """
        .trimIndent()
    )
  }

  @Test
  @Transactional
  fun `正常 创建实体时 应成功保存并返回实体`() {
    // 创建实体
    val entity = testService.createEntity("test", 100)

    // 验证返回实体
    assertNotNull(entity)
    assertTrue(entity.id > 0)
    assertEquals("test", entity.name)
    assertEquals(100, entity.value)

    // 验证数据库持久化
    val found = testService.findEntity(entity.id)
    assertNotNull(found)
    assertEquals("test", found.name)
    assertEquals(100, found.value)
  }

  @Test
  @Transactional
  fun `正常 只读事务中查询实体 应成功返回`() {
    // 先创建实体
    val entity = testService.createEntity("readonly", 300)
    assertNotNull(entity)

    // 在只读事务中查询
    val found = testService.findEntity(entity.id)
    assertNotNull(found)
    assertEquals("readonly", found.name)
    assertEquals(300, found.value)
  }

  @Test
  @Transactional
  fun `边界 创建实体时使用空字符串 应成功保存`() {
    val entity = testService.createEntity("", 0)
    assertNotNull(entity)
    assertEquals("", entity.name)
    assertEquals(0, entity.value)

    val found = testService.findEntity(entity.id)
    assertNotNull(found)
    assertEquals("", found.name)
    assertEquals(0, found.value)
  }

  @Test
  @Transactional
  fun `边界 创建实体时使用最大整数值 应成功保存`() {
    val entity = testService.createEntity("max", Int.MAX_VALUE)
    assertNotNull(entity)
    assertEquals("max", entity.name)
    assertEquals(Int.MAX_VALUE, entity.value)

    val found = testService.findEntity(entity.id)
    assertNotNull(found)
    assertEquals("max", found.name)
    assertEquals(Int.MAX_VALUE, found.value)
  }

  @Test
  @Transactional
  fun `异常 查询不存在的实体ID 应返回null`() {
    val found = testService.findEntity(Long.MAX_VALUE)
    assertNull(found)
  }
}
