package io.tn.rds.service

import io.tn.rds.dto.FsDto
import io.tn.rds.dto.PageParam
import io.tn.rds.dto.PagedData
import io.tn.rds.util.PageWrap
import io.tn.rds.vo.FileVo
import jakarta.validation.Valid

interface FileService {
  fun saveFile(
    @Valid f: FsDto?
  ): FileVo?

  fun listFiles(pageParam: PageParam = PageWrap.DEFAULT_PARAM): PagedData<FileVo>
}
