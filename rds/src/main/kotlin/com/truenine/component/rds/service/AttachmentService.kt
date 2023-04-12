package com.truenine.component.rds.service

import com.truenine.component.rds.models.req.PutAttachmentRequestParam
import com.truenine.component.rds.base.PagedRequestParam
import com.truenine.component.rds.base.PagedResponseResult
import com.truenine.component.rds.util.PagedWrapper
import com.truenine.component.rds.models.AttachmentModel
import jakarta.validation.Valid

interface AttachmentService {
  fun saveAttachment(
    @Valid f: PutAttachmentRequestParam?
  ): AttachmentModel?

  fun listFiles(pagedRequestParam: PagedRequestParam = PagedWrapper.ZERO): PagedResponseResult<AttachmentModel>
}
