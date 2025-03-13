plugins { `kotlinspring-convention` }

version = libs.versions.compose.rds.get()

dependencies { implementation(libs.org.babyfish.jimmer.jimmer.sql.kotlin) }
