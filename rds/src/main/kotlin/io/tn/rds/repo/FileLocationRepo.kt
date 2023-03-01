package io.tn.rds.repo

import io.tn.rds.base.BaseRepo
import io.tn.rds.dao.FileLocationDao
import org.springframework.stereotype.Repository

@Repository
interface FileLocationRepo : BaseRepo<FileLocationDao, String> {
  fun findByUrl(url: String): FileLocationDao?
}
