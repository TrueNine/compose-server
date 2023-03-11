package com.truenine.component.rds.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FileVoRepo :
  JpaRepository<com.truenine.component.rds.vo.FileVo, String>
