package net.yan100.compose.rds.crud.entities.jpa

import net.yan100.compose.meta.annotations.MetaDef
import net.yan100.compose.rds.core.entities.IJpaEntity

/**
 * 数据库版的 kv 值缓存
 *
 * 不能进行索引，只能作为 缓存服务器配置持久化的一种手段
 */
@MetaDef
interface SuperCommonKvConfigDbCache : IJpaEntity {
  /** ## 配置 key */
  var k: String

  /** ## 配置 json value */
  var v: String?
}
