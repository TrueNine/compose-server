package net.yan100.compose.client.domain.entries

import net.yan100.compose.client.collectImports
import net.yan100.compose.client.domain.TsModifier
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.toRenderCode
import net.yan100.compose.client.toVariableName

sealed class TsFile<T : TsFile<T>>(
  open val fileName: TsName,
  open val fileExt: String = "ts",
  open val imports: List<TsImport> = emptyList(),
  open val scopes: List<TsScope<*>> = emptyList(),
  open val usedNames: List<TsName> = emptyList(),
  open val exports: List<TsExport> = emptyList(),
) {
  inner class CodeBuildable<T : TsFile<T>>(
    private val builder: Appendable = StringBuilder()
  ) : Appendable by builder {
    override fun toString(): String = builder.toString()
  }

  abstract val render: CodeBuildable<T>.(file: T) -> Unit

  @Suppress("UNCHECKED_CAST")
  val code: String
    get() {
      val builder = CodeBuildable<T>()
      render(builder, this as T)
      return builder.toString()
    }

  data class SingleServiceClass(
    val serviceClassScope: TsScope.Class
  ) : TsFile<SingleServiceClass>(
    imports = serviceClassScope.collectImports(),
    scopes = listOf(serviceClassScope),
    exports = listOf(TsExport.ExportedDefined(serviceClassScope.name)),
    usedNames = listOf(serviceClassScope.name),
    fileName = serviceClassScope.name,
  ) {
    override val render: CodeBuildable<SingleServiceClass>.(SingleServiceClass) -> Unit = { file ->
      val classScope = file.serviceClassScope
      appendLine(file.imports.toRenderCode())
      append("${TsModifier.Export} ")
      append("${classScope.modifier.marker} ")
      append("${classScope.name.toVariableName()} ")
      appendLine(classScope.scopeQuota.left)
      appendLine(classScope.scopeQuota.right)
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
    override val render: CodeBuildable<SingleInterface>.(SingleInterface) -> Unit = { file ->
      val name = file.interfaces.name.toVariableName()
      appendLine(imports.toRenderCode())
      append("${TsModifier.Export} ")
      append("${file.interfaces.modifier.marker} ")
      append(name)
      if (file.interfaces.generics.isNotEmpty()) append(file.interfaces.generics.toRenderCode())
      val superTypes = file.interfaces.superTypes
      if (file.interfaces.superTypes.isNotEmpty()) {
        append(" ${TsModifier.Extends} ")
        val superTypeNames = superTypes.joinToString(separator = ", ") { superType ->
          superType.toString()
        }
        append(superTypeNames)
      }
      appendLine(" ${file.interfaces.scopeQuota.left}")
      val properties = file.interfaces.properties.joinToString(",\n") {
        "  $it"
      }
      appendLine(properties)
      append(file.interfaces.scopeQuota.right)
      appendLine()

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
    override val render: CodeBuildable<SingleTypeAlias>.(SingleTypeAlias) -> Unit = { file ->
      val name = file.typeAlias.name.toVariableName()
      if (imports.isNotEmpty()) {
        appendLine(imports.toRenderCode())
        appendLine()
      }
      append("${TsModifier.Export} ")
      append(file.typeAlias.modifier.marker)
      append(" ")
      append(name)
      if (file.typeAlias.generics.isNotEmpty()) append(file.typeAlias.generics.toRenderCode())
      append(" = ")
      append(file.typeAlias.aliasFor.toString())
      appendLine()
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
    override val render: CodeBuildable<SingleTypeUtils>.(SingleTypeUtils) -> Unit,
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
    override val render: CodeBuildable<SingleEnum>.(SingleEnum) -> Unit = {
      val name = when (enums.name) {
        is TsName.Name -> enums.name.name
        is TsName.PathName -> enums.name.name
        else -> error("enum name ${enums.name} is not supported")
      }
      append("${TsModifier.Export} ")
      append(enums.modifier.marker)
      appendLine(" $name ${enums.scopeQuota.left}")
      val constants = enums.constants.map { (k, v) ->
        """  $k = ${if (v is String) "'$v'" else v}"""
      }.joinToString(",\n")
      appendLine(constants)
      append(enums.scopeQuota.right)
      appendLine()
    }

  }
}
