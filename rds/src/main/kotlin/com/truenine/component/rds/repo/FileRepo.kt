package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.dao.FileDao
import org.springframework.stereotype.Repository

@Repository
interface FileRepo : BaseRepo<FileDao, String>
