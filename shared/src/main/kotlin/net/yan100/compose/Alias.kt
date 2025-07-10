package net.yan100.compose

import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import net.yan100.compose.domain.IPage
import net.yan100.compose.domain.IPageParam
import net.yan100.compose.domain.IPageParamLike
import net.yan100.compose.typing.ISO4217
import org.slf4j.Logger

typealias string = String

typealias byte = Byte

typealias int = Int

typealias short = Short

typealias float = Float

typealias double = Double

typealias long = Long

typealias decimal = BigDecimal

typealias bigint = BigInteger

typealias Timestamp = Long

typealias timestamp = Timestamp

typealias date = LocalDate

typealias time = LocalTime

typealias datetime = LocalDateTime

/** 数据库主键 */
typealias Id = Long

/** @see Id */
typealias RefId = Id

typealias Pq = IPageParam

typealias PqLike = IPageParamLike

typealias Pr<T> = IPage<T>

typealias ISO4217Typing = ISO4217

typealias Currency = ISO4217

// === rust alias ===
typealias bool = Boolean

typealias i8 = Byte

typealias i16 = Short

typealias i32 = Int

typealias i64 = Long

typealias u8 = UByte

typealias u16 = UShort

typealias u32 = UInt

typealias u64 = ULong

typealias f32 = Float

typealias f64 = Double

typealias char = Char

typealias SysLogger = Logger
