package com.truenine.component.rds.service

import com.truenine.component.rds.dao.DeleteBackupDao

interface BackupService {
  fun save(backupData: Any?): DeleteBackupDao?
}
