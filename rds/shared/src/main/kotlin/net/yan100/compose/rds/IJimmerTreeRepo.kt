package net.yan100.compose.rds

import net.yan100.compose.rds.entities.IJimmerTreeEntity
import org.springframework.data.repository.NoRepositoryBean

@Deprecated("树结构已废弃")
@NoRepositoryBean
interface IJimmerTreeRepo<T : IJimmerTreeEntity, ID : Any> : IJimmerRepo<T, ID>
