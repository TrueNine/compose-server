package net.yan100.compose.client.domain.entries

import net.yan100.compose.client.collectImports
import net.yan100.compose.client.domain.TsScope
import net.yan100.compose.client.domain.TsTypeVal
import net.yan100.compose.client.toRenderCode
import net.yan100.compose.client.toVariableName

sealed class TsFile(
  open val fileName: TsName,
  open val ext: String = "ts",
  open val imports: List<TsImport> = emptyList(),
  open val scopes: List<TsScope> = emptyList(),
  open val usedNames: List<TsName> = emptyList(),
  open val exports: List<TsExport> = emptyList(),
  open val code: String = ""
) {
  /**
   * 单接口文件
   */
  data class SingleInterface(
    val interfaces: TsScope.Interface,
    override val fileName: TsName = interfaces.name,
    override val ext: String = "ts"
  ) : TsFile(
    imports = interfaces.collectImports(),
    scopes = listOf(interfaces),
    exports = listOf(TsExport.ExportedDefined(fileName)),
    usedNames = listOf(interfaces.name),
    ext = ext,
    fileName = fileName,
    code = buildString {
      val imports = interfaces.collectImports()
      val name = interfaces.name.toVariableName()
      if (imports.isNotEmpty()) {
        appendLine(imports.toRenderCode())
        appendLine()
      }
      append("export ")
      append(interfaces.modifier.marker)

      if (interfaces.superTypes.isEmpty()) {
        append(" extends")
        interfaces.superTypes.forEach { superType ->
          when (superType) {
            is TsTypeVal.TypeDef -> {
              append(" ${superType.typeName.toVariableName()},")
            }

            else -> {}
          }
          removeSuffix(",")
        }
      }

      appendLine(" $name ${interfaces.scopeQuota.left}")
      val properties = interfaces.properties.joinToString(",\n") {
        "${it.name}: ${TsTypeVal.Any}"
      }
      appendLine(properties)
      append(interfaces.scopeQuota.right)
    }
  )

  /**
   * 单工具类文件
   */
  data class SingleTypeUtils(
    override val fileName: TsName,
    override val code: String,
    override val scopes: List<TsScope> = emptyList(),
    override val usedNames: List<TsName> = emptyList(),
    val exportName: TsExport = TsExport.ExportedDefined(fileName),
    override val imports: List<TsImport> = emptyList(),
  ) : TsFile(
    fileName = fileName,
    code = code,
    imports = imports,
    exports = listOf(exportName),
    scopes = scopes,
    usedNames = usedNames + fileName,
    ext = "ts"
  )

  /**
   * 单枚举文件
   */
  data class SingleEnum(
    val enums: TsScope.Enum,
    override val fileName: TsName = enums.name,
    override val scopes: List<TsScope> = listOf(enums),
  ) : TsFile(
    usedNames = listOf(fileName),
    scopes = scopes,
    fileName = fileName,
    exports = listOf(
      TsExport.ExportedDefined(enums.name)
    ),
    code = buildString {
      val name = when (enums.name) {
        is TsName.Name -> enums.name.name
        is TsName.PathName -> enums.name.name
        else -> error("enum name ${enums.name} is not supported")
      }
      append("export ")
      append(enums.modifier.marker)
      appendLine(" $name ${enums.scopeQuota.left}")
      val constants = enums.constants.map { (k, v) ->
        """  $k = ${if (v is String) "'$v'" else v}"""
      }.joinToString(",\n")
      appendLine(constants)
      append(enums.scopeQuota.right)
      appendLine()
    }
  )
}
