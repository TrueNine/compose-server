package net.yan100.compose.rds.core

import net.yan100.compose.rds.core.entities.IJimmerTreeEntity
import org.springframework.data.repository.NoRepositoryBean


@NoRepositoryBean
interface IJimmerTreeRepo<T : IJimmerTreeEntity, ID : Any> : IJimmerRepo<T, ID> {

  fun findChildrenCount(parent: T) {
    val kc = parent::class
  }
}

