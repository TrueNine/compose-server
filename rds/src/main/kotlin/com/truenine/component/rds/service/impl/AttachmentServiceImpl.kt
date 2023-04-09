package com.truenine.component.rds.service.impl

import com.truenine.component.rds.entity.AttachmentEntity
import com.truenine.component.rds.entity.AttachmentLocationEntity
import com.truenine.component.rds.models.req.PutAttachmentRequestParam
import com.truenine.component.rds.base.PageModelRequestParam
import com.truenine.component.rds.base.PagedResponseResult
import com.truenine.component.rds.repo.AttachmentLocationRepo
import com.truenine.component.rds.repo.AttachmentRepo
import com.truenine.component.rds.repo.AttachmentModelRepo
import com.truenine.component.rds.service.AttachmentService
import com.truenine.component.rds.util.PagedResponseResultWrapper
import com.truenine.component.rds.models.AttachmentModel
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class AttachmentServiceImpl(
  private val attachmentRepo: AttachmentRepo,
  private val locationRepo: AttachmentLocationRepo,
  private val attachmentModelRepo: AttachmentModelRepo
) : AttachmentService {

  @Transactional(rollbackFor = [Exception::class])
  override fun saveFile(
    @Valid f: PutAttachmentRequestParam?
  ): AttachmentModel? = f?.let {
    AttachmentLocationEntity()
      .apply {
      doc = it.doc
      name = it.dir
      baseUrl = "${it.url}/${it.dir}"
      rn(it.rnType)
    }.run {
      // 保存文件地址
      locationRepo.findByBaseUrl(baseUrl)
        ?: locationRepo.save(this)
    }.let { location ->
      val file = AttachmentEntity()
        .apply {
        attachmentLocationId = location.id
        this.size = f.size
        this.metaName = f.fullName
        this.mimeType = f.mimeType
        this.saveName = f.saveName
      }.run {
        // 保存文件
        attachmentRepo.save(this)
      }
      // 转换为vo
      AttachmentModel().apply {
        this.baseUrl = location.baseUrl
        this.size = file.size
        this.id = file.id
        this.saveName = file.saveName
        this.mimeType = file.mimeType
        this.name = file.metaName
      }
    }
  }

  override fun listFiles(pageModelRequestParam: PageModelRequestParam): PagedResponseResult<AttachmentModel> {
    return PagedResponseResultWrapper.data(
      attachmentModelRepo.findAll(PagedResponseResultWrapper.param(pageModelRequestParam))
    )
  }
}
