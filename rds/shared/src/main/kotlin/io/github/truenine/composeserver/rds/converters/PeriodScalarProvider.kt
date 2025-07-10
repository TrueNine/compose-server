package io.github.truenine.composeserver.rds.converters

import java.time.Period
import org.babyfish.jimmer.sql.runtime.ScalarProvider

class PeriodScalarProvider : ScalarProvider<Period?, String?> {
  override fun toScalar(sqlValue: String): Period? {
    return Period.parse(sqlValue)
  }

  override fun toSql(scalarValue: Period): String {
    return scalarValue.toString()
  }
}
