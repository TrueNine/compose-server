package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.dao.FileLocationDao
import org.springframework.stereotype.Repository

@Repository
interface FileLocationRepo : BaseRepo<FileLocationDao, String> {
  fun findByUrl(url: String): FileLocationDao?
}
