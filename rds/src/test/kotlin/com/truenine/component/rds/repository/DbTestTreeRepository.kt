package com.truenine.component.rds.repository

import com.truenine.component.rds.base.TreeRepository
import com.truenine.component.rds.entity.DbTestTreeEntity
import org.springframework.stereotype.Repository

@Repository
interface DbTestTreeRepository :
  TreeRepository<DbTestTreeEntity> {
}
