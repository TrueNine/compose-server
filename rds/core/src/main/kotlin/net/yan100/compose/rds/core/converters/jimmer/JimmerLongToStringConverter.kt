package net.yan100.compose.rds.core.converters.jimmer

import net.yan100.compose.core.RefId
import org.babyfish.jimmer.jackson.Converter
import org.babyfish.jimmer.jackson.LongToStringConverter

class JimmerLongToStringConverter : Converter<RefId, String> by LongToStringConverter()
