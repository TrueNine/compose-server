package com.truenine.component.rds.repo.uni

import com.truenine.component.rds.models.AttachmentModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AttachmentModelRepo : JpaRepository<AttachmentModel, Long>
