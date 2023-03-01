package io.tn.rds.repo

import io.tn.rds.base.BaseRepo
import io.tn.rds.dao.FileDao
import org.springframework.stereotype.Repository

@Repository
interface FileRepo : BaseRepo<FileDao, String> {
}
