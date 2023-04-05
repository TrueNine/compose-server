package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.dao.AttachmentLocationDao
import org.springframework.stereotype.Repository

@Repository
interface AttachmentLocationRepo : BaseRepo<AttachmentLocationDao, String> {
  fun findByBaseUrl(baseUrl: String): AttachmentLocationDao?
}
