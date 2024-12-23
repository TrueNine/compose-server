package net.yan100.compose.client.domain

import net.yan100.compose.client.templates.EnumTemplate

sealed class TypescriptFile(
  open val fileType: TypescriptFileType,
  open val name: String,
  open val usedNames: List<String> = emptyList(),
  open val imports: List<TypescriptImport> = emptyList(),
  open val exports: List<TypescriptExportNameScope> = emptyList(),
  open val code: String = "",
  open val ext: String = "ts"
) {
  /**
   * 单工具类文件
   */
  data class SingleUtils(
    override val name: String,
    override val code: String,
    override val usedNames: List<String> = emptyList(),
    val exportName: TypescriptExportNameScope = TypescriptExportNameScope(name),
    override val fileType: TypescriptFileType = TypescriptFileType.STATIC_UTILS,
    override val imports: List<TypescriptImport> = emptyList(),
  ) : TypescriptFile(
    fileType = fileType,
    name = name,
    code = code,
    imports = imports,
    exports = listOf(exportName),
    usedNames = usedNames + name,
    ext = "ts"
  )

  data class Default(
    override val name: String,
    override val fileType: TypescriptFileType,
    override val code: String = ""
  ) : TypescriptFile(
    fileType = fileType,
    name = name
  )

  data class Enum(
    val tsEnum: TypescriptEnum,
    override val name: String = tsEnum.name,
    override val fileType: TypescriptFileType = TypescriptFileType.DOMAIN_ENUM_CLASS
  ) : TypescriptFile(
    fileType = fileType,
    usedNames = listOf(name),
    name = name,
    code = EnumTemplate.renderEnum(tsEnum),
    exports = listOf(
      TypescriptExportNameScope(
        name = name
      )
    )
  )
}
