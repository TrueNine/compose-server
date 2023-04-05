package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.dao.AttachmentDao
import org.springframework.stereotype.Repository

@Repository
interface AttachmentRepo : BaseRepo<AttachmentDao, String>
