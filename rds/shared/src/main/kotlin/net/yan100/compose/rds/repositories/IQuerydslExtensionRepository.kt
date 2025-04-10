package net.yan100.compose.rds.repositories

import net.yan100.compose.rds.entities.IJpaEntity
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface IQuerydslExtensionRepository<T : IJpaEntity> :
  IBaseRepository<T>, QuerydslPredicateExecutor<T>
