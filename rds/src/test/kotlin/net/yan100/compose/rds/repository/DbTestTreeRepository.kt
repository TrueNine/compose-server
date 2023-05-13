package net.yan100.compose.rds.repository

import net.yan100.compose.rds.base.TreeRepository
import net.yan100.compose.rds.entity.DbTestTreeEntity
import org.springframework.stereotype.Repository

@Repository
interface DbTestTreeRepository :
  TreeRepository<DbTestTreeEntity>
