package com.truenine.component.rds.service.impl

import com.truenine.component.rds.base.BaseServiceImpl
import com.truenine.component.rds.entity.AttachmentEntity
import com.truenine.component.rds.repository.AttachmentRepository
import com.truenine.component.rds.service.AttachmentService
import org.springframework.stereotype.Service

@Service
class AttachmentServiceImpl(repo: AttachmentRepository) : AttachmentService, BaseServiceImpl<AttachmentEntity>(repo) {
}
