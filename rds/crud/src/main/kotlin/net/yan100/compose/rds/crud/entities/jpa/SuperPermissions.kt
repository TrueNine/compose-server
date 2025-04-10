package net.yan100.compose.rds.crud.entities.jpa

import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.entities.IJpaEntity

/**
 * # 权限
 *
 * @author TrueNine
 * @since 2023-01-02
 */
@MetaDef
interface SuperPermissions : IJpaEntity {
  /** 权限名 */
  var name: String

  /** 权限描述 */
  var doc: String?
}
