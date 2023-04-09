package com.truenine.component.rds.gen.ctx

import com.truenine.component.core.consts.DataBaseBasicFieldNames
import com.truenine.component.core.lang.DTimer
import com.truenine.component.core.lang.Str
import com.truenine.component.rds.gen.util.Case
import lombok.extern.slf4j.Slf4j
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Slf4j
class DefCtx {
  private var genLang: String = "java"
  private var dbName: String = ""
  private var pkgName: String = ""
  private var ignoreCols: MutableSet<String> = mutableSetOf()
  private var baseEntityClass: String = "io.tn.rds.base.BaseDao"
  private var baseRepositoryClass: String =
    "io.tn.rds.base.BaseRepo"
  private var baseServiceClass: String = ""
  private var baseServiceImplClass: String = ""
  private var author: String = "Generator Author"
  private var entity: String = "entity"
  private var repo: String = "repository"
  private var service: String = "service"
  private var entitySuffix: String = "Entity"
  private var repoSuffix: String = "Repository"
  private var serviceSuffix: String = "Service"
  private var serviceImplSuffix: String = "ServiceImpl"
  private var serviceImpl: String = "${service}.impl"

  init {
    this.ignoreCols += DataBaseBasicFieldNames.getAll()
  }

  fun lang(mode: String): DefCtx {
    this.genLang = mode
    return this
  }

  fun getLang(): String {
    return this.genLang
  }

  fun getEntityBaseClassPath(): String {
    return this.baseEntityClass.split(".").last()
  }

  fun author(author: String): DefCtx {
    this.author = author
    return this
  }

  fun getAuthor(): String {
    return this.author
  }

  fun entityBaseClassName(typeName: String): DefCtx {
    this.baseEntityClass = typeName
    return this
  }

  fun repositoryBaseClassName(typeName: String): DefCtx {
    this.baseRepositoryClass = typeName
    return this
  }

  fun serviceBaseClassName(typeName: String): DefCtx {
    this.baseServiceClass = typeName
    return this
  }

  fun serviceImplBaseClassName(typeName: String): DefCtx {
    this.baseServiceImplClass = typeName
    return this
  }

  fun getBaseEntityClassType(): String {
    return baseEntityClass
  }

  fun getBaseRepositoryClassType(): String {
    return baseRepositoryClass
  }

  fun getBaseServiceClassType(): String {
    return baseServiceClass
  }

  fun getBaseServiceImplClassType(): String {
    return baseServiceImplClass
  }

  fun ignoreColumns(ignore: (MutableSet<String>) -> MutableSet<String>): DefCtx {
    this.ignoreCols = ignore.invoke(DataBaseBasicFieldNames.getAll().toMutableSet())
    return this
  }

  fun getIgnoreColumns(): Set<String> {
    return this.ignoreCols
  }

  fun genPkg(pkg: String): DefCtx {
    this.pkgName = pkg
    return this
  }

  fun repo(pkg: String, suffix: String): DefCtx {
    this.repo = pkg
    this.repoSuffix = suffix
    return this
  }

  fun service(pkg: String, suffix: String): DefCtx {
    this.service = pkg
    this.serviceSuffix = suffix
    return this
  }

  fun serviceImpl(pkg: String, suffix: String): DefCtx {
    this.serviceImpl = pkg
    this.serviceImplSuffix = suffix
    return this
  }

  fun dao(pkg: String, suffix: String): DefCtx {
    this.entity = pkg
    this.entitySuffix = suffix
    return this
  }

  fun getEntityPkg(): String {
    return "${getPkg()}${if (Str.hasText(entity)) "." else ""}$entity"
  }

  fun getRepositoryPkg(): String {
    return "${getPkg()}${if (Str.hasText(repo)) "." else ""}$repo"
  }

  fun getServicePkg(): String {
    return "${getPkg()}${if (Str.hasText(service)) "." else ""}$service"
  }

  fun getServiceImplPkg(): String {
    return "${getPkg()}${if (Str.hasText(serviceImpl)) "." else ""}$serviceImpl"
  }

  fun getEntitySuffix(): String {
    return this.entitySuffix
  }

  fun getServiceSuffix(): String {
    return this.serviceSuffix
  }

  fun getServiceImplSuffix(): String {
    return this.serviceImplSuffix
  }

  fun getRepositorySuffix(): String {
    return this.repoSuffix
  }

  fun getEntityPkgPath(): String {
    return getEntityPkg().replace(".", "/")
  }

  fun getRepositoryPkgPath(): String {
    return getRepositoryPkg().replace(".", "/")
  }

  fun getServicePkgPath(): String {
    return getServicePkg().replace(".", "/")
  }

  fun getServiceImplPkgPath(): String {
    return getServiceImplPkg().replace(".", "/")
  }

  fun getPkgPath(): String {
    return this.pkgName.replace(".", "/")
  }

  fun getPkg(): String {
    return this.pkgName
  }

  fun getDbName(): String {
    return this.dbName
  }

  fun db(name: String): DefCtx {
    this.dbName = name
    return this
  }

  fun nowDay(): String {
    return DateTimeFormatter.ofPattern(DTimer.DATE).format(LocalDate.now())
  }

  fun lover(f: String): String {
    return Case.firstLover(f)
  }

  fun upper(f: String): String {
    return Case.firstUpper(f)
  }

  fun getBaseEntityClassName(): String {
    return getBaseEntityClassType().split(".").last()
  }

  fun getBaseRepositoryClassName(): String {
    return getBaseRepositoryClassType().split(".").last()
  }

  fun getBaseServiceClassName(): String {
    return getBaseServiceClassType().split(".").last()
  }

  fun getBaseServiceImplClassName(): String {
    return getBaseServiceImplClassType().split(".").last()
  }
}
