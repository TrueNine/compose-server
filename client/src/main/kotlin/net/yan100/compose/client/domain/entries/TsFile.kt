package net.yan100.compose.client.domain.entries

import net.yan100.compose.client.*
import net.yan100.compose.client.domain.*


sealed class TsFile<T : TsFile<T>>(
  open val fileName: TsName,
  open val fileExt: String = "ts",
  open val imports: List<TsImport> = emptyList(),
  open val scopes: List<TsScope<*>> = emptyList(),
  open val usedNames: List<TsName> = emptyList(),
  open val exports: List<TsExport> = emptyList(),
) {

  abstract val render: CodeRender<T>

  @Suppress("UNCHECKED_CAST")
  val code: String
    get() = CodeBuildable<T>().also {
      render(it, this as T)
    }.toString()

  data class ServiceClass(
    val serviceClassScope: TsScope.Class,
    val optionsName: String = "${serviceClassScope.name.toVariableName()}ApiOptions",
    override val imports: List<TsImport> = serviceClassScope.collectImports() + TsImport(
      useType = true,
      usingNames = listOf("Executor".toTsName()),
      fromPath = "../../Executor"
    )
  ) : TsFile<ServiceClass>(
    imports = imports + listOf(
      TsImport(
        usingNames = listOf("Executor".toTsName()),
        useType = true,
        fromPath = "../../Executor" // TODO 写死的值
      )
    ),
    scopes = listOf(serviceClassScope),
    exports = listOf(
      TsExport.ExportedDefined(serviceClassScope.name),
      TsExport.ExportedDefined(optionsName.toTsName())
    ),
    usedNames = listOf(serviceClassScope.name),
    fileName = serviceClassScope.name,
  ) {
    override val render: CodeRender<ServiceClass> = { file ->
      val classScope = file.serviceClassScope
      imports(file.imports)
      exportScope(classScope) {
        if (classScope.functions.isNotEmpty()) {
          indent()
          space("constructor(private executor: Executor)")
          inlineScope(TsScopeQuota.OBJECT)
        }
        line()
        classScope.functions.forEach { f ->
          indent()
          f.modifiers.forEach { m -> space(m) }
          code(f.name.name)
          inlineScope(TsScopeQuota.ASSIGNMENT) {
            bracketInlineScope {
              """options: ${optionsName}['${f.name}']"""
            }
            inlineScope(TsScopeQuota.ARROW) {
              spaces(f.returnType.toString(), TsScopeQuota.ASSIGN)
            }
          }
          if (f.isAsync) space(TsModifier.Async)
          inlineScope(TsScopeQuota.BRACKETS) {
            code(f.params.joinToString(",") { it.name.toString() })
          }
          inlineScope(TsScopeQuota.ARROW)
          scope(TsScopeQuota.OBJECT) {
            f.code.lines().filter { it.isNotBlank() }.forEach {
              line(it.trim())
            }
          }
        }
      }
      line()
      spaces(TsModifier.Export, TsTypeModifier.Type, optionsName)
      scope(TsScopeQuota.ASSIGN_OBJECT) {
        classScope.functions.forEach { f ->
          indent()
          space("""'${f.name.name}':""")
          if (f.params.isNotEmpty()) {
            scope(TsScopeQuota.OBJECT) {
              f.params.forEach { p ->
                indent()
                space(p.name.toString())
                space(":")
                code(p.typeVal.toString())
                code("\n")
              }
            }
          } else code(TsTypeVal.EmptyObject)
          code("\n")
        }
      }
    }
  }

  /**
   * 单接口文件
   */
  data class SingleInterface(
    val interfaces: TsScope.Interface,
  ) : TsFile<SingleInterface>(
    imports = interfaces.collectImports(),
    scopes = listOf(interfaces),
    exports = listOf(TsExport.ExportedDefined(interfaces.name)),
    usedNames = listOf(interfaces.name),
    fileName = interfaces.name,
  ) {
    override val render: CodeRender<SingleInterface> = { file ->
      imports(file.imports)
      exportScope(file.interfaces) {
        val properties = file.interfaces.properties.joinToString(",\n") {
          it.toString()
        }
        line(properties)
      }
    }
  }

  data class SingleTypeAlias(
    val typeAlias: TsScope.TypeAlias,
  ) : TsFile<SingleTypeAlias>(
    imports = typeAlias.collectImports(),
    scopes = listOf(typeAlias),
    exports = listOf(TsExport.ExportedDefined(typeAlias.name)),
    usedNames = listOf(typeAlias.name),
    fileName = typeAlias.name,
  ) {
    override val render: CodeRender<SingleTypeAlias> = { file ->
      val name = file.typeAlias.name.toVariableName()
      imports(file.imports)
      space(TsModifier.Export)
      space(file.typeAlias.modifier)
      space(name)
      if (file.typeAlias.generics.isNotEmpty()) space(file.typeAlias.generics.toRenderCode())
      space("=")
      line(file.typeAlias.aliasFor.toString())
    }
  }

  /**
   * 单工具类文件
   */
  data class SingleTypeUtils(
    override val fileName: TsName,
    override val scopes: List<TsScope<*>> = emptyList(),
    override val usedNames: List<TsName> = emptyList(),
    val exportName: TsExport = TsExport.ExportedDefined(fileName),
    override val imports: List<TsImport> = emptyList(),
    override val render: CodeRender<SingleTypeUtils>,
  ) : TsFile<SingleTypeUtils>(
    fileName = fileName,
    imports = imports,
    exports = listOf(exportName),
    scopes = scopes,
    usedNames = usedNames + fileName,
    fileExt = "ts"
  )

  /**
   * 单枚举文件
   */
  data class SingleEnum(
    val enums: TsScope.Enum,
    override val fileName: TsName = enums.name,
    override val scopes: List<TsScope<*>> = listOf(enums),
  ) : TsFile<SingleEnum>(
    usedNames = listOf(fileName),
    scopes = scopes,
    fileName = fileName,
    exports = listOf(TsExport.ExportedDefined(enums.name)),
  ) {
    override val render: CodeRender<SingleEnum> = { file ->
      exportScope(file.enums) { e ->
        val constants = e.constants.map { (k, v) ->
          """$k = ${if (v is String) "'$v'" else v}"""
        }.joinToString(",\n")
        line(constants)
      }
    }
  }
}
