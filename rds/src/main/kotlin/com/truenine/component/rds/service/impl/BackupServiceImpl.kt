package com.truenine.component.rds.service.impl

import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.databind.ObjectMapper
import com.truenine.component.core.lang.KtLogBridge
import com.truenine.component.rds.dao.DeleteBackupDao
import com.truenine.component.rds.repo.DeleteBackupRepo
import com.truenine.component.rds.service.BackupService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
open class BackupServiceImpl(
  private val backupRepo: DeleteBackupRepo,
  private val mapper: ObjectMapper
) : BackupService {

  @Transactional(rollbackFor = [Exception::class])
  override fun save(backupData: Any?) = backupData?.run {
    val serJson = backupData.run {
      mapper
        .setAnnotationIntrospector(AnnotationIntrospector.nopInstance())
        .writeValueAsString(backupData)
    }
    val backup =
      DeleteBackupDao()
        .apply {
          delSerObj = serJson
          namespaces = backupData.javaClass.typeName
          delSysVersion = "1.0"
        }
    backupRepo.save(backup)
  }

  companion object {
    @JvmStatic
    private val log = KtLogBridge.getLog(BackupServiceImpl::class.java)
  }
}
