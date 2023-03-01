package io.tn.rds.service.impl

import io.tn.rds.dao.FileDao
import io.tn.rds.dao.FileLocationDao
import io.tn.rds.dto.FsDto
import io.tn.rds.dto.PageParam
import io.tn.rds.dto.PagedData
import io.tn.rds.repo.FileLocationRepo
import io.tn.rds.repo.FileRepo
import io.tn.rds.repo.FileVoRepo
import io.tn.rds.service.FileService
import io.tn.rds.util.PageWrap
import io.tn.rds.vo.FileVo
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
