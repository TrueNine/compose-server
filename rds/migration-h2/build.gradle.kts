plugins {
  `java-convention`
  `sqlmigration-convention`
}

version = libs.versions.compose.rds.migration.h2.get()
