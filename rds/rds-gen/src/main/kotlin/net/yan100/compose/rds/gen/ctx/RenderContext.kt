package net.yan100.compose.rds.gen.ctx

import lombok.extern.slf4j.Slf4j
import net.yan100.compose.core.lang.DTimer
import net.yan100.compose.rds.base.BaseRepository
import net.yan100.compose.rds.gen.util.DbCaseConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Slf4j
class RenderContext {
  private var genLang: String = "java"
  private var dbName: String = ""
  private var pkgName: String = ""
  private var ignoreColumns: MutableSet<String> = mutableSetOf()
  private var baseEntityClass: String = net.yan100.compose.rds.base.BaseEntity::class.java.canonicalName
  private var baseRepositoryClass: String = BaseRepository::class.java.canonicalName
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
    this.ignoreColumns += net.yan100.compose.core.consts.DataBaseBasicFieldNames.getAll()
  }

  fun lang(mode: String): RenderContext {
    this.genLang = mode
    return this
  }

  fun getLang(): String {
    return this.genLang
  }

  fun getEntityBaseClassPath(): String {
    return this.baseEntityClass.split(".").last()
  }

  fun author(author: String): RenderContext {
    this.author = author
    return this
  }

  fun getAuthor(): String {
    return this.author
  }

  fun entityBaseClassName(typeName: String): RenderContext {
    this.baseEntityClass = typeName
    return this
  }

  fun repositoryBaseClassName(typeName: String): RenderContext {
    this.baseRepositoryClass = typeName
    return this
  }

  fun serviceBaseClassName(typeName: String): RenderContext {
    this.baseServiceClass = typeName
    return this
  }

  fun serviceImplBaseClassName(typeName: String): RenderContext {
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

  fun ignoreColumns(ignore: (MutableSet<String>) -> MutableSet<String>): RenderContext {
    this.ignoreColumns = ignore.invoke(net.yan100.compose.core.consts.DataBaseBasicFieldNames.getAll().toMutableSet())
    return this
  }

  fun getIgnoreColumns(): Set<String> {
    return this.ignoreColumns
  }

  fun packageName(pkg: String): RenderContext {
    this.pkgName = pkg
    return this
  }

  fun repo(pkg: String, suffix: String): RenderContext {
    this.repo = pkg
    this.repoSuffix = suffix
    return this
  }

  fun service(pkg: String, suffix: String): RenderContext {
    this.service = pkg
    this.serviceSuffix = suffix
    return this
  }

  fun serviceImpl(pkg: String, suffix: String): RenderContext {
    this.serviceImpl = pkg
    this.serviceImplSuffix = suffix
    return this
  }

  fun entity(packageName: String = "entity", suffix: String = "Entity"): RenderContext {
    this.entity = packageName
    this.entitySuffix = suffix
    return this
  }

  fun getEntityPkg(): String {
    return "${getPkg()}${if (net.yan100.compose.core.lang.Str.hasText(entity)) "." else ""}$entity"
  }

  fun getRepositoryPkg(): String {
    return "${getPkg()}${if (net.yan100.compose.core.lang.Str.hasText(repo)) "." else ""}$repo"
  }

  fun getServicePkg(): String {
    return "${getPkg()}${if (net.yan100.compose.core.lang.Str.hasText(service)) "." else ""}$service"
  }

  fun getServiceImplPkg(): String {
    return "${getPkg()}${if (net.yan100.compose.core.lang.Str.hasText(serviceImpl)) "." else ""}$serviceImpl"
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

  private fun getPkg(): String {
    return this.pkgName
  }

  fun getDbName(): String {
    return this.dbName
  }

  fun db(name: String): RenderContext {
    this.dbName = name
    return this
  }

  fun nowDay(): String {
    return DateTimeFormatter.ofPattern(DTimer.DATE).format(LocalDate.now())
  }

  fun lover(f: String): String {
    return DbCaseConverter.firstLover(f)
  }

  fun upper(f: String): String {
    return DbCaseConverter.firstUpper(f)
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