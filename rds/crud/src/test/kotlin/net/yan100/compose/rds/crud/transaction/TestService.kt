package net.yan100.compose.rds.crud.transaction

import org.babyfish.jimmer.sql.ast.mutation.SaveMode
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.annotation.Propagation

@Service
class TestService(
  private val sqlClient: KSqlClient
) {
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  fun createEntity(name: String, value: Int): TestEntity {
    return sqlClient.saveCommand(
      TestEntity {
        this.name = name
        this.value = value
      }, SaveMode.INSERT_ONLY
    ).execute().modifiedEntity
  }

  @Transactional(rollbackFor = [Exception::class])
  fun createEntityWithException(name: String, value: Int): TestEntity {
    val entity = createEntity(name, value)
    throw RuntimeException("模拟事务回滚")
  }

  @Transactional(readOnly = true)
  fun findEntity(id: Long): TestEntity? {
    return sqlClient.findById(TestEntity::class, id)
  }
} 
