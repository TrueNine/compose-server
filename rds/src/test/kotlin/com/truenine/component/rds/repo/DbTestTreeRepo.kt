package com.truenine.component.rds.repo

import com.truenine.component.rds.base.TreeRepo
import com.truenine.component.rds.entity.DbTestPresortTreeEntity
import org.springframework.stereotype.Repository

@Repository
interface DbTestTreeRepo :
  TreeRepo<DbTestPresortTreeEntity> {
}
