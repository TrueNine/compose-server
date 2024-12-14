package net.yan100.compose.rds.core

import net.yan100.compose.rds.core.entities.IJimmerPersistentEntity
import org.babyfish.jimmer.spring.repository.KRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface IJimmerRepo<E : IJimmerPersistentEntity, ID : Any> : KRepository<E, ID> {
  fun IJimmerRepo<E, ID>.findByIdOrNull(id: ID): E? = findById(id).orElse(null)
}
