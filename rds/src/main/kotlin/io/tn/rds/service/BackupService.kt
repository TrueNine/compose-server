package io.tn.rds.service

import io.tn.rds.dao.DeleteBackupDao

interface BackupService {
  fun save(backupData: Any?): DeleteBackupDao?
}
