package io.github.truenine.composeserver.rds

import io.github.truenine.composeserver.rds.entities.IPersistentEntity
import org.babyfish.jimmer.spring.repository.KRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface IRepo<E : IPersistentEntity, ID : Any> : KRepository<E, ID> {
  fun IRepo<E, ID>.findByIdOrNull(id: ID): E? = findById(id).orElse(null)
}
