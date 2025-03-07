package net.yan100.compose.rds.core.repositories

import net.yan100.compose.core.Id
import net.yan100.compose.core.Pq
import net.yan100.compose.core.Pr
import net.yan100.compose.rds.core.entities.IJpaEntity
import net.yan100.compose.rds.core.toPageable
import net.yan100.compose.rds.core.toPr
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface IBaseRepository<T : IJpaEntity> :
  IPersistentRepository<T>,
  JpaRepository<T, Id>,
  CrudRepository<T, Id>,
  JpaSpecificationExecutor<T> {
  fun findAll(pq: Pq?): Pr<T> = findAll(pq.toPageable()).toPr()
}
