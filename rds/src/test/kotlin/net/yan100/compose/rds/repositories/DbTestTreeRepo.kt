package net.yan100.compose.rds.repositories

import net.yan100.compose.rds.entities.DbTestTreeEntity
import net.yan100.compose.rds.repositories.base.ITreeRepo
import org.springframework.stereotype.Repository

@Repository
interface DbTestTreeRepo : ITreeRepo<DbTestTreeEntity>
