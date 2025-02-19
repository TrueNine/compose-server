package net.yan100.compose.rds.core.converters.jimmer

import org.babyfish.jimmer.sql.runtime.ScalarProvider
import java.time.Duration

class DurationScalarProvider : ScalarProvider<Duration?, String?> {
  override fun toScalar(sqlValue: String): Duration? {
    return Duration.parse(sqlValue)
  }

  override fun toSql(scalarValue: Duration): String {
    return scalarValue.toString()
  }
}
