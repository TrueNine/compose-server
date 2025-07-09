plugins { id("buildlogic.kotlinspring-conventions") }

version = libs.versions.compose.rds.get()

dependencies { implementation(libs.org.babyfish.jimmer.jimmer.sql.kotlin) }
