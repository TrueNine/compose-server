package com.truenine.component.rds.repo

import com.truenine.component.rds.base.BaseRepo
import com.truenine.component.rds.entity.AttachmentEntity
import org.springframework.stereotype.Repository

@Repository
interface AttachmentRepo : BaseRepo<AttachmentEntity, String>
