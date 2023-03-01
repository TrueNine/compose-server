package com.truenine.component.rds.service

import com.truenine.component.rds.dto.FsDto
import com.truenine.component.rds.dto.PageParam
import com.truenine.component.rds.dto.PagedData
import com.truenine.component.rds.util.PageWrap
import com.truenine.component.rds.vo.FileVo
import jakarta.validation.Valid

interface FileService {
  fun saveFile(
    @Valid f: FsDto?
  ): FileVo?

  fun listFiles(pageParam: PageParam = PageWrap.DEFAULT_PARAM): PagedData<FileVo>
}
