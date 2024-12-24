package net.yan100.compose.client.domain.entries

import net.yan100.compose.client.domain.TypescriptScope

sealed class TypescriptFile(
  open val fileName: TypescriptName,
  open val usedNames: List<TypescriptName> = emptyList(),
  open val scopes: List<TypescriptScope> = emptyList(),
  open val imports: List<TypescriptImport> = emptyList(),
  open val exports: List<TypescriptExport> = emptyList(),
  open val code: String = "",
  open val ext: String = "ts"
) {

  /**
   * 单工具类文件
   */
  data class SingleTypeUtils(
    override val fileName: TypescriptName,
    override val code: String,
    override val scopes: List<TypescriptScope> = emptyList(),
    override val usedNames: List<TypescriptName> = emptyList(),
    val exportName: TypescriptExport = TypescriptExport.ExportedDefine(fileName),
    override val imports: List<TypescriptImport> = emptyList(),
  ) : TypescriptFile(
    fileName = fileName,
    code = code,
    imports = imports,
    exports = listOf(exportName),
    scopes = scopes,
    usedNames = usedNames + fileName,
    ext = "ts"
  )


  data class SingleEnum(
    val typescriptEnum: TypescriptScope.Enum,
    override val fileName: TypescriptName = typescriptEnum.name,
    override val scopes: List<TypescriptScope> = listOf(typescriptEnum),
  ) : TypescriptFile(
    usedNames = listOf(fileName),
    scopes = scopes,
    fileName = fileName,
    exports = listOf(
      TypescriptExport.ExportedDefine(typescriptEnum.name)
    ),
    code = buildString {
      append("export ")
      append(typescriptEnum.modifier.marker)
      appendLine(" ${typescriptEnum.name.name} ${typescriptEnum.scopeQuota.left}")
      val constants = typescriptEnum.constants.map { (k, v) ->
        """  $k = ${if (v is String) "\"$v\"" else v}"""
      }.joinToString(",\n")
      appendLine(constants)
      append(typescriptEnum.scopeQuota.right)
      appendLine()
    }
  )
}
