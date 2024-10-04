package net.yan100.compose.rds.core.repositories

import jakarta.validation.Valid
import net.yan100.compose.core.Id
import net.yan100.compose.core.Pq
import net.yan100.compose.core.Pr
import net.yan100.compose.rds.core.entities.IEntity
import net.yan100.compose.rds.core.page
import net.yan100.compose.rds.core.toPageable
import net.yan100.compose.rds.core.toPr
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface IBaseRepository<T : IEntity> :
  IAnyRepository<T>,
  JpaRepository<T, Id>,
  CrudRepository<T, Id>,
  JpaSpecificationExecutor<T> {
  fun findAll(@Valid pq: Pq?): Pr<T> = findAll(pq.toPageable()).toPr()
}
