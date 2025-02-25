package net.yan100.compose.client.domain.entries

sealed class TsExport {
  /** 该文件已具有一些导出，只做声明 */
  data class ExportedDefined(val name: TsName) : TsExport()

  /** `export {name,name as newName ...}` */
  data class Export(val names: List<TsName>) : TsExport()

  /** `export * as asName from 'fromPath'` */
  data class ExternalImportAllNamingAsExport(
    val fromPath: String,
    val asName: TsName.As,
  )

  /** `export * from 'xxx'` */
  data class ExternalImportDefaultExport(val fromPath: String)

  /** 语法结构为 `export { usingNames.a,usingNames.b ... } from 'xxx'` */
  data class ExternalImportUsingExport(
    val fromPath: String,
    val usingNames: List<TsName>,
  )
}
