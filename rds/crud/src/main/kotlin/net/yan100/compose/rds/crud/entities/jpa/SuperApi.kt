package net.yan100.compose.rds.crud.entities.jpa

import net.yan100.compose.RefId
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.meta.annotations.MetaSkipGeneration
import net.yan100.compose.meta.annotations.orm.MetaFormula
import net.yan100.compose.rds.entities.IJpaEntity

/**
 * api
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@MetaDef
interface SuperApi : IJpaEntity {
  /** ## 权限 id */
  var permissionsId: RefId?

  /** 名称 */
  var name: String?

  /** 描述 */
  var doc: String?

  /** 路径 */
  var apiPath: String?

  /** 请求方式 */
  var apiMethod: String?

  /** 请求协议 */
  var apiProtocol: String?

  @MetaFormula
  @MetaSkipGeneration
  val uriDeep: Int
    get() = apiPath?.split("/")?.filter { it.isNotBlank() }?.size ?: 0
}
