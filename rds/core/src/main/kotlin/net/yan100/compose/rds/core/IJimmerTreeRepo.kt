package net.yan100.compose.rds.core

import net.yan100.compose.rds.core.entities.IJimmerTreeEntity
import org.springframework.data.repository.NoRepositoryBean

@Deprecated("树结构已废弃")
@NoRepositoryBean
interface IJimmerTreeRepo<T : IJimmerTreeEntity, ID : Any> : IJimmerRepo<T, ID>
