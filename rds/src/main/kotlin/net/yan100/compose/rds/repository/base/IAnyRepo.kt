package net.yan100.compose.rds.repository.base

import net.yan100.compose.rds.core.entity.AnyEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean

/**
 * # 任意实体通用 CRUD 接口
 * @author TrueNine
 * @since 2023-05-05
 */
@JvmDefaultWithCompatibility
@NoRepositoryBean
interface IAnyRepo<T : AnyEntity> :
  JpaRepository<T, String>,
  CrudRepository<T, String>,
  JpaSpecificationExecutor<T> {

}

