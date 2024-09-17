package net.yan100.compose.rds.core.repositories

import net.yan100.compose.rds.core.entities.IEntity
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface IQuerydslExtensionRepository<T : IEntity> : IBaseRepository<T>, QuerydslPredicateExecutor<T>
