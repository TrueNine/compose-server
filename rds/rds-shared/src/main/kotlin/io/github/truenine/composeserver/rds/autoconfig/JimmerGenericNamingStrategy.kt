package io.github.truenine.composeserver.rds.autoconfig

import org.babyfish.jimmer.meta.ImmutableProp
import org.babyfish.jimmer.sql.meta.DatabaseNamingStrategy
import org.babyfish.jimmer.sql.runtime.DefaultDatabaseNamingStrategy

/** Custom Jimmer database naming strategy adjustments */
class JimmerGenericNamingStrategy(private val defaultDelegate: DefaultDatabaseNamingStrategy = DefaultDatabaseNamingStrategy.LOWER_CASE) :
  DatabaseNamingStrategy by defaultDelegate {
  override fun middleTableName(prop: ImmutableProp?): String {
    return defaultDelegate.middleTableName(prop).removeSuffix("_mapping")
  }
}
