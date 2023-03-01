package io.tn.rds.service.impl

import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.databind.ObjectMapper
import io.tn.core.lang.KtLogBridge
import io.tn.rds.dao.DeleteBackupDao
import io.tn.rds.repo.DeleteBackupRepo
import io.tn.rds.service.BackupService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

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
    val backup = DeleteBackupDao().apply {
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
