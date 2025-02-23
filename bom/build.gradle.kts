plugins {
  `java-platform`
  `publish-convention`
}

version = libs.versions.compose.asProvider().get()

dependencies {
  constraints {
    //api(projects.gradlePlugin)
    api(projects.cacheable)
    api(projects.core)
    api(projects.client)
    api(projects.data.dataExtract)
    api(projects.data.dataCrawler)
    api(projects.depend.dependHttpExchange)
    api(projects.depend.dependJackson)
    api(projects.depend.dependPaho)
    api(projects.depend.dependServlet)
    api(projects.depend.dependSpringdocOpenapi)
    api(projects.depend.dependXxlJob)

    api(projects.ksp.kspClient)
    api(projects.ksp.kspPlugin)
    api(projects.ksp.kspToolkit)
    api(projects.meta)
    api(projects.oss)
    api(projects.pay)
    api(projects.rds.rdsCore)
    api(projects.rds.rdsCrud)
    api(projects.rds.rdsMigrationH2)
    api(projects.rds.rdsMigrationMysql)
    api(projects.rds.rdsMigrationPostgres)
    api(projects.security.securityCrypto)
    api(projects.security.securityOauth2)
    api(projects.security.securitySpring)
    api(projects.testtoolkit)
    api(projects.versionCatalog)
  }
}
