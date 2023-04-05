package com.truenine.component.rds.service

import com.truenine.component.rds.models.req.PutAttachmentRequestParam
import com.truenine.component.rds.base.PageModelRequestParam
import com.truenine.component.rds.base.PagedResponseResult
import com.truenine.component.rds.util.PagedResponseResultWrapper
import com.truenine.component.rds.models.AttachmentModel
import jakarta.validation.Valid

interface AttachmentService {
  fun saveFile(
    @Valid f: PutAttachmentRequestParam?
  ): AttachmentModel?

  fun listFiles(pageModelRequestParam: PageModelRequestParam = PagedResponseResultWrapper.ZERO): PagedResponseResult<AttachmentModel>
}
