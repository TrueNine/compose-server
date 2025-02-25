plugins { `kotlinspring-convention` }

version = libs.versions.compose.rds.jimmer.ext.postgres.get()

dependencies { implementation(libs.org.babyfish.jimmer.jimmer.sql.kotlin) }
