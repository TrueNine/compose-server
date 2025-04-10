package net.yan100.compose.rds.repositories

import net.yan100.compose.Id
import net.yan100.compose.Pq
import net.yan100.compose.Pr
import net.yan100.compose.rds.entities.IJpaEntity
import net.yan100.compose.rds.toPageable
import net.yan100.compose.rds.toPr
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
