package net.yan100.compose.rds.repository

import net.yan100.compose.rds.entity.DbTestTreeEntity
import net.yan100.compose.rds.repository.base.ITreeRepo
import org.springframework.stereotype.Repository

@Repository
interface DbTestTreeRepository : ITreeRepo<DbTestTreeEntity>
