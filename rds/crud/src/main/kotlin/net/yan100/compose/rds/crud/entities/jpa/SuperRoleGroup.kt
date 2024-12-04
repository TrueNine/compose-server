package net.yan100.compose.rds.crud.entities.jpa

import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IJpaEntity

@MetaDef
interface SuperRoleGroup : IJpaEntity {
  /** 名称 */
  var name: String

  /** 描述 */
  var doc: String?
}
