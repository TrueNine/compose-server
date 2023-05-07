package com.truenine.component.rds.service

import com.truenine.component.rds.base.BaseService
import com.truenine.component.rds.base.PagedRequestParam
import com.truenine.component.rds.base.PagedResponseResult
import com.truenine.component.rds.entity.AttachmentEntity
import com.truenine.component.rds.util.PagedWrapper


interface AttachmentService : BaseService<AttachmentEntity> {
  fun findFullUrlById(id: Long): String?
  fun findAllFullUrlByMetaNameStartingWith(metaName: String, page: PagedRequestParam = PagedWrapper.DEFAULT_MAX): PagedResponseResult<String>
}
