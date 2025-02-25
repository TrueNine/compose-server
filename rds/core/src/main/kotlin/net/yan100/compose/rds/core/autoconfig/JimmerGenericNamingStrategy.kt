package net.yan100.compose.rds.core.autoconfig

import org.babyfish.jimmer.meta.ImmutableProp
import org.babyfish.jimmer.sql.meta.DatabaseNamingStrategy
import org.babyfish.jimmer.sql.runtime.DefaultDatabaseNamingStrategy

/** jimmer 自定义命名策略更改 */
class JimmerGenericNamingStrategy(
  private val defaultDelegate: DefaultDatabaseNamingStrategy =
    DefaultDatabaseNamingStrategy.LOWER_CASE
) : DatabaseNamingStrategy by defaultDelegate {
  override fun middleTableName(prop: ImmutableProp?): String {
    return defaultDelegate.middleTableName(prop).removeSuffix("_mapping")
  }
}
