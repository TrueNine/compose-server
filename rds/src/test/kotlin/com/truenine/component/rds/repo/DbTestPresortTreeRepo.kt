package com.truenine.component.rds.repo

import com.truenine.component.rds.base.PresortTreeRepo
import com.truenine.component.rds.entity.DbTestPresortTreeEntity
import org.springframework.stereotype.Repository

@Repository
interface DbTestPresortTreeRepo :
  PresortTreeRepo<DbTestPresortTreeEntity, String> {
}
