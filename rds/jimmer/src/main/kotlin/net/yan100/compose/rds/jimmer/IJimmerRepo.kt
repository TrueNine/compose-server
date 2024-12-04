package net.yan100.compose.rds.jimmer

import net.yan100.compose.rds.jimmer.entities.IEntity
import org.babyfish.jimmer.spring.repository.KRepository
import org.springframework.data.repository.NoRepositoryBean
import kotlin.jvm.optionals.getOrNull

@NoRepositoryBean
interface IJimmerRepo<E : IEntity, ID : Any> : KRepository<E, ID> {
  fun IJimmerRepo<E, ID>.findByIdOrNull(id: ID): E? = findById(id).getOrNull()
}
