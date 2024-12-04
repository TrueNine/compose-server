package net.yan100.compose.rds.crud.entities.jpa

import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IJpaEntity

@MetaDef
interface SuperRole : IJpaEntity {
  /** 角色名称 */
  var name: String

  /** 角色描述 */
  var doc: String?
}
