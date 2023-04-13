package com.truenine.component.rds.service.aggregator

import com.truenine.component.rds.base.PagedRequestParam
import com.truenine.component.rds.base.PagedResponseResult
import com.truenine.component.rds.entity.AttachmentEntity
import com.truenine.component.rds.entity.AttachmentLocationEntity
import com.truenine.component.rds.models.AttachmentModel
import com.truenine.component.rds.models.req.PutAttachmentRequestParam
import com.truenine.component.rds.repo.AttachmentLocationRepo
import com.truenine.component.rds.repo.uni.AttachmentModelRepo
import com.truenine.component.rds.repo.AttachmentRepo
import com.truenine.component.rds.util.PagedWrapper
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class AttachmentModelServiceImpl(
  private val attachmentRepo: AttachmentRepo,
  private val locationRepo: AttachmentLocationRepo,
  private val attachmentModelRepo: AttachmentModelRepo
) : AttachmentModelService {

  @Transactional(rollbackFor = [Exception::class])
  override fun saveAttachment(
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
        val file = AttachmentEntity().apply {
          attachmentLocationId = location.id
          size = f.size
          metaName = f.fullName
          mimeType = f.mimeType
          saveName = f.saveName
        }
        // 保存文件
        attachmentRepo.save(file)
        // 转换为文件形式
        AttachmentModel().apply {
          baseUrl = location.baseUrl
          size = file.size
          id = file.id
          saveName = file.saveName
          mimeType = file.mimeType
          name = file.metaName
        }
      }
  }

  override fun listFiles(@Valid pagedRequestParam: PagedRequestParam): PagedResponseResult<AttachmentModel> = PagedWrapper.result(
    attachmentModelRepo.findAll(PagedWrapper.param(pagedRequestParam))
  )

}
