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
    // Manually create test table
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
  fun `should save and return entity successfully`() {
    // Create entity
    val entity = testService.createEntity("test", 100)

    // Verify returned entity
    assertNotNull(entity)
    assertTrue(entity.id > 0)
    assertEquals("test", entity.name)
    assertEquals(100, entity.value)

    // Verify persistence in database
    val found = testService.findEntity(entity.id)
    assertNotNull(found)
    assertEquals("test", found.name)
    assertEquals(100, found.value)
  }

  @Test
  @Transactional
  fun `should query entity successfully in read-only transaction`() {
    // Create entity first
    val entity = testService.createEntity("readonly", 300)
    assertNotNull(entity)

    // Query in read-only transaction
    val found = testService.findEntity(entity.id)
    assertNotNull(found)
    assertEquals("readonly", found.name)
    assertEquals(300, found.value)
  }

  @Test
  @Transactional
  fun `boundary should save entity with empty string`() {
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
  fun `boundary should save entity with max integer value`() {
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
  fun `error querying non-existent entity id should return null`() {
    val found = testService.findEntity(Long.MAX_VALUE)
    assertNull(found)
  }
}
