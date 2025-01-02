package net.yan100.compose.client.domain.entries

import net.yan100.compose.client.*
import net.yan100.compose.client.domain.TsModifier
import net.yan100.compose.client.domain.TsScope


sealed class TsFile<T : TsFile<T>>(
  open val fileName: TsName,
  open val fileExt: String = "ts",
  open val imports: List<TsImport> = emptyList(),
  open val scopes: List<TsScope<*>> = emptyList(),
  open val usedNames: List<TsName> = emptyList(),
  open val exports: List<TsExport> = emptyList(),
) {

  abstract val render: FileRender<T>

  @Suppress("UNCHECKED_CAST")
  val code: String
    get() = CodeBuildable<T>().also {
      render(it, this as T)
    }.toString()

  data class ServiceClass(
    val serviceClassScope: TsScope.Class,
    override val imports: List<TsImport> = serviceClassScope.collectImports() + TsImport(
      useType = true,
      usingNames = listOf("Executor".toTsName()),
      fromPath = "../../Executor"
    )
  ) : TsFile<ServiceClass>(
    imports = imports,
    scopes = listOf(serviceClassScope),
    exports = listOf(TsExport.ExportedDefined(serviceClassScope.name)),
    usedNames = listOf(serviceClassScope.name),
    fileName = serviceClassScope.name,
  ) {
    override val render: FileRender<ServiceClass> = { file ->
      val classScope = file.serviceClassScope
      imports(file.imports)
      exportScope(classScope) {
        line("123")
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
    override val render: FileRender<SingleInterface> = { file ->
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
    override val render: FileRender<SingleTypeAlias> = { file ->
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
    override val render: FileRender<SingleTypeUtils>,
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
    override val render: FileRender<SingleEnum> = { file ->
      exportScope(file.enums) { e ->
        val constants = e.constants.map { (k, v) ->
          """$k = ${if (v is String) "'$v'" else v}"""
        }.joinToString(",\n")
        line(constants)
      }
    }
  }
}
