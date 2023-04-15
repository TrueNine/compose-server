package com.truenine.component.rds.base

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface AnyRepository<T : AnyEntity> :
  JpaRepository<T, Long>,
  CrudRepository<T, Long>,
  JpaSpecificationExecutor<T> {
}
