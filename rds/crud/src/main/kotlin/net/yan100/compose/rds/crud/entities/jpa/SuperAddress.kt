package net.yan100.compose.rds.crud.entities.jpa

import net.yan100.compose.core.bool
import net.yan100.compose.core.domain.Coordinate
import net.yan100.compose.core.int
import net.yan100.compose.core.string
import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IJpaTreeEntity

@MetaDef
interface SuperAddress : IJpaTreeEntity {
  /** 代码 */
  var code: string

  /** 名称 */
  var name: string

  /** 级别 0 为国家 */
  var level: int?

  /** 年份版本号 */
  var yearVersion: string?

  /** 定位 */
  var center: Coordinate?

  /** 是否为终结地址（如市辖区） */
  var leaf: bool?
}
