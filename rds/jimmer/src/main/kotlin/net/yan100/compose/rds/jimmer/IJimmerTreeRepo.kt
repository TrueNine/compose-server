package net.yan100.compose.rds.jimmer

import net.yan100.compose.rds.jimmer.entities.ITreeEntity
import org.springframework.data.repository.NoRepositoryBean


@NoRepositoryBean
interface IJimmerTreeRepo<T : ITreeEntity, ID : Any> : IJimmerRepo<T, ID> {

  fun findChildrenCount(parent: T) {
    val kc = parent::class
  }
}

