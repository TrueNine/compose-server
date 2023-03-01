package io.tn.rds.gen.util

import io.tn.rds.gen.ctx.DefCtx
import io.tn.rds.gen.ctx.JavaTable
import io.tn.rds.gen.dao.DataBaseTable
import io.tn.rds.gen.db.Dbc
import io.tn.rds.gen.dto.JavaColumnDto
import io.tn.rds.gen.template.TemplateScope
import io.tn.rds.gen.util.converter.AbstractTypeConverter
import io.tn.rds.gen.util.converter.MysqlConverter

class Gen {
  private var ctx: DefCtx = DefCtx()
  private val h: Dbc = Dbc
  private val t: TemplateScope = TemplateScope

  fun config(cfg: (DefCtx) -> DefCtx): Gen {
    this.ctx = cfg.invoke(ctx)
    return this
  }

  fun run(mode: String = "java"): Gen {
    val mysqlConverter = MysqlConverter()
    ctx.lang(mode)
    ctx.let { c ->
      val db = h.queryDb(ctx.getDbName())
      db.forEach {
        templateGenerate(c, it, mysqlConverter)
      }
    }
    return this
  }

  private fun templateGenerate(
      c: DefCtx,
      it: DataBaseTable,
      mysqlConverter: AbstractTypeConverter
  ) {
    val table = converterTable(it, mysqlConverter)
    t.scoped(
      pkgPath = c.getEntityPkgPath(),
      genFileName = "${Case.firstUpper(it.name!!)}${ctx.getEntitySuffix()}.${c.getLang()}",
      ftlName = "Entity.${c.getLang()}",
      ctx = c,
      tab = table
    )

    t.scoped(
      pkgPath = c.getRepositoryPkgPath(),
      genFileName = "${Case.firstUpper(it.name!!)}${ctx.getRepositorySuffix()}.${c.getLang()}",
      ftlName = "Repository.${c.getLang()}",
      ctx = c,
      tab = table
    )

    t.scoped(
      pkgPath = c.getServicePkgPath(),
      genFileName = "${Case.firstUpper(it.name!!)}${ctx.getServiceSuffix()}.${c.getLang()}",
      ftlName = "Service.${c.getLang()}",
      ctx = c,
      tab = table
    )

    t.scoped(
      pkgPath = c.getServiceImplPkgPath(),
      genFileName = "${Case.firstUpper(it.name!!)}${ctx.getServiceImplSuffix()}.${c.getLang()}",
      ftlName = "ServiceImpl.${c.getLang()}",
      ctx = c,
      tab = table
    )
  }

  private fun converterTable(
      table: DataBaseTable,
      cor: AbstractTypeConverter
  ): JavaTable {
    val cols = h.queryTable(table.name!!).map {
      JavaColumnDto().apply {
        comment = it.comment
        dbType = it.type
        javaType = cor.getConverter(it.type!!).typeName
        colName = it.field
        fieldName = ctx.lover(it.field!!)
        upperName = it.field!!.uppercase()
        nullable = it.nullable
        unique = it.key == "UNI"
        defSql = it.type!!.uppercase()
        defaultValue = it.defaultValue
      }
    }.filter { !ctx.getIgnoreColumns().contains(it.colName) }.toMutableList()
    val idx = h.queryIndex(table.name!!).filter { it.keyName != "PRIMARY" }
    val result = JavaTable()
    result.className = Case.firstUpper(table.name!!)
    result.imports = cols.filter {
      it.dbType != null
    }.mapNotNull { cor.getConverter(it.dbType!!).importPkg }
      .toMutableSet()
    result.name = table.name
    result.comment = table.comment
    result.columns = cols
    result.idx = idx.toMutableList()
    return result
  }
}
