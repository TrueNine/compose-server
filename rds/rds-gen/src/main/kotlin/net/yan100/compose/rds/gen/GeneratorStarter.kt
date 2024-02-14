package net.yan100.compose.rds.gen


import net.yan100.compose.rds.core.consts.BaseTableNames
import net.yan100.compose.rds.gen.converter.AbstractTypeConverter
import net.yan100.compose.rds.gen.converter.MysqlConverter
import net.yan100.compose.rds.gen.ctx.RenderContext
import net.yan100.compose.rds.gen.ctx.TableContext
import net.yan100.compose.rds.gen.entity.TableEntity
import net.yan100.compose.rds.gen.models.ColumnModel
import net.yan100.compose.rds.gen.util.DbCaseConverter
import net.yan100.compose.rds.gen.util.HutoolDbConnector
import net.yan100.compose.rds.gen.util.TemplateRender

class GeneratorStarter {
    private var ctx: RenderContext = RenderContext()
    private val connector: HutoolDbConnector = HutoolDbConnector
    private val t: TemplateRender = TemplateRender

    fun config(cfg: (RenderContext) -> RenderContext): GeneratorStarter {
        this.ctx = cfg(ctx)
        return this
    }

    fun run(mode: String = "java"): GeneratorStarter {
        val mysqlConverter = MysqlConverter()
        ctx.lang(mode)
        ctx.db(
            connector.queryCurrentDbName()!!
        )
        ctx.let { c ->
            val db = connector.queryDb(ctx.getDbName())
            db.filter {
                !BaseTableNames.all().contains(it.name)
            }.forEach {
                templateGenerate(c, it, mysqlConverter)
            }
        }
        return this
    }

    private fun templateGenerate(
        renderContext: RenderContext,
        tableEntity: TableEntity,
        converter: AbstractTypeConverter
    ) {
        val table = getTableContext(tableEntity, converter)
        t.render(
            packagePath = renderContext.getEntityPkgPath(),
            generatedFileName = "${DbCaseConverter.firstUpper(tableEntity.name!!)}${ctx.getEntitySuffix()}.${renderContext.getLang()}",
            templateFileNamePrefix = "Entity.${renderContext.getLang()}",
            renderContext = renderContext,
            tableContext = table
        )

        t.render(
            packagePath = renderContext.getRepositoryPkgPath(),
            generatedFileName = "${DbCaseConverter.firstUpper(tableEntity.name!!)}${ctx.getRepositorySuffix()}.${renderContext.getLang()}",
            templateFileNamePrefix = "Repository.${renderContext.getLang()}",
            renderContext = renderContext,
            tableContext = table
        )

        t.render(
            packagePath = renderContext.getServicePkgPath(),
            generatedFileName = "${DbCaseConverter.firstUpper(tableEntity.name!!)}${ctx.getServiceSuffix()}.${renderContext.getLang()}",
            templateFileNamePrefix = "Service.${renderContext.getLang()}",
            renderContext = renderContext,
            tableContext = table
        )

        t.render(
            packagePath = renderContext.getServiceImplPkgPath(),
            generatedFileName = "${DbCaseConverter.firstUpper(tableEntity.name!!)}${ctx.getServiceImplSuffix()}.${renderContext.getLang()}",
            templateFileNamePrefix = "ServiceImpl.${renderContext.getLang()}",
            renderContext = renderContext,
            tableContext = table
        )
    }

    private fun getTableContext(
        tableEntity: TableEntity,
        converter: AbstractTypeConverter
    ): TableContext {
        val cols = connector.queryColumnModel(tableEntity.name!!).map {
            ColumnModel().apply {
                comment = it.comment
                dbType = it.type
                javaType = converter.getConverterTypeModel(it.type!!).typeName
                colName = it.field
                fieldName = ctx.lover(it.field!!)
                upperName = it.field!!.uppercase()
                nullable = it.nullable
                unique = it.key == "UNI"
                defSql = it.type!!.uppercase()
                defaultValue = it.defaultValue
            }
        }.filter { !ctx.getIgnoreColumns().contains(it.colName) }.toMutableList()

        val idx = connector.queryIndex(tableEntity.name!!)
            .filter { it.keyName != "PRIMARY" }
            .filter { !net.yan100.compose.core.consts.DataBaseBasicFieldNames.getAll().contains(it.keyName) }

        val result = TableContext()
        result.className = DbCaseConverter.firstUpper(tableEntity.name!!)
        result.imports = cols.filter {
            it.dbType != null
        }.mapNotNull { converter.getConverterTypeModel(it.dbType!!).importPkg }
            .toMutableSet()
        result.name = tableEntity.name
        result.comment = tableEntity.comment
        result.columns = cols
        result.idx = idx.toMutableList()
        return result
    }
}
