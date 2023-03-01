package com.truenine.component.rds.service.impl

import com.truenine.component.rds.dao.FileDao
import com.truenine.component.rds.dao.FileLocationDao
import com.truenine.component.rds.dto.FsDto
import com.truenine.component.rds.dto.PageParam
import com.truenine.component.rds.dto.PagedData
import com.truenine.component.rds.repo.FileLocationRepo
import com.truenine.component.rds.repo.FileRepo
import com.truenine.component.rds.repo.FileVoRepo
import com.truenine.component.rds.service.FileService
import com.truenine.component.rds.util.PageWrap
import com.truenine.component.rds.vo.FileVo
import jakarta.validation.Valid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class FileServiceImpl(
  private val fileRepo: FileRepo,
  private val locationRepo: FileLocationRepo,
  private val fileVoRepo: FileVoRepo
) : FileService {

  @Transactional(rollbackFor = [Exception::class])
  override fun saveFile(
    @Valid f: FsDto?
  ): FileVo? = f?.let {
    val descriptor = getDescriptor(f.fullName)
    FileLocationDao().apply {
      doc = it.doc
      name = it.dir
      url = "${it.url}/${it.dir}"
      rn(it.rnType)
    }.run {
      // 保存文件地址
      locationRepo.findByUrl(url)
        ?: locationRepo.save(this)
    }.let { location ->
      val file = FileDao().apply {
        fileLocationId = location.id
        this.descriptor = descriptor
        this.byteSize = f.byteSize
        this.metaName = f.fullName
        this.mimeType = f.mimeType
        this.saveName = f.saveName
      }.run {
        // 保存文件
        fileRepo.save(this)
      }
      // 转换为vo
      FileVo().apply {
        this.url = location.url
        this.byteSize = file.byteSize
        this.id = file.id
        this.saveName = file.saveName
        this.mimeType = file.mimeType
        this.name = file.metaName
      }
    }
  }

  override fun listFiles(pageParam: PageParam): PagedData<FileVo> {
    return PageWrap.data(
      fileVoRepo.findAll(PageWrap.param(pageParam))
    )
  }

  private val sep = "."
  private fun getDescriptor(name: String?): String? {
    return name?.let {
      if (it.startsWith(sep)) {
        return@let ""
      } else {
        return@let it.split(sep).last()
      }
    }
  }
}
